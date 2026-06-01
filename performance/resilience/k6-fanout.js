import ws from 'k6/ws';
import http from 'k6/http';
import {check, group, sleep} from 'k6';
import {Counter, Gauge, Trend} from 'k6/metrics';

const jwts = JSON.parse(open("./jwts.json"));

const NUM_USERS = 500;
const NUM_SERVERS = 1; // Set to 1 as per current local setup
const CHAT_ID = 'group_5k_perf_test';

// Get server host/port from environment variable (e.g. localhost:8080)
const SERVER_URL = __ENV.SERVER_URL || 'localhost:8080';

const wsConnectSuccess = new Counter('ws_connect_success');
const wsConnectFailure = new Counter('ws_connect_failure');
const wsActiveConnections = new Gauge('ws_active_connections');

const messagesReceived = new Counter('messages_received');
const messagesPerUser = new Trend('messages_per_user');
const e2eLatency = new Trend('e2e_delivery_latency_ms');
const fanoutErrors = new Counter('fanout_errors');

const restApiSuccess = new Counter('rest_api_success');
const restApiFailure = new Counter('rest_api_failure');

export const options = {
    scenarios: {
        subscribers: {
            executor: 'constant-vus',
            vus: 500,
            duration: '2m',
            exec: 'subscriberScenario',
        },
        publishers: {
            executor: 'constant-arrival-rate',
            rate: 30,
            timeUnit: '1s',
            startTime: '15s', // Wait for subscribers to connect
            duration: '105s',
            preAllocatedVUs: 30,
            maxVUs: 500,
            exec: 'publisherScenario',
        },
    },
    thresholds: {
        'ws_connect_success': ['rate > 0.95'],
        'messages_received': ['count > 100000'], // More realistic for 30msg/s * 500 users
        'messages_per_user': ['p(95) > 100'],
        'e2e_delivery_latency_ms': ['p(95) < 3000'], // 95% within 3s
    },
};


function generateUserData(userIndex) {
    return {
        username: `user_${String(userIndex + 1)}`,
        name: `Performance User ${userIndex + 1}`
    };
}

function getServerByRoundRobin(userIndex, isWebSocket = true) {
    // If NUM_SERVERS is 1, use the provided SERVER_URL directly
    if (NUM_SERVERS === 1) {
        return isWebSocket ? `ws://${SERVER_URL}` : `http://${SERVER_URL}`;
    }

    // Otherwise, maintain round-robin logic but based on the base port extracted from SERVER_URL
    const parts = SERVER_URL.split(':');
    const host = parts[0];
    const basePort = parseInt(parts[1], 10) || 8080;
    const port = basePort + (userIndex % NUM_SERVERS);
    return isWebSocket ? `ws://${host}:${port}` : `http://${host}:${port}`;
}

export function subscriberScenario() {
    const vuId = __VU;
    const userIndex = (vuId - 1) % NUM_USERS;

    // Random jitter to prevent thundering herd
    sleep(Math.random() * 10);

    const userData = generateUserData(userIndex);

    let accessToken = login(userData);

    if (!accessToken) {
        console.error(`Failed to login subscriber ${userData.username}`);
        return;
    }

    group('WebSocket Subscribe', () => {
        connectAndSubscribe(userIndex, userData, accessToken);
    });
}

export function publisherScenario() {
    const vuId = __VU;
    const userIndex = vuId % NUM_USERS;

    const userData = generateUserData(userIndex);

    let accessToken = login(userData);

    if (!accessToken) {
        console.error(`Failed to login publisher ${userData.username}`);
        return;
    }

    group('Publish Message', () => {
        publishMessageViaREST(userIndex, userData, accessToken);
    });

}

function login(userData) {
    return jwts[userData.username].token;
}

function connectAndSubscribe(userIndex, userData, accessToken) {
    const serverUrl = getServerByRoundRobin(userIndex, true);
    const wsEndpoint = `${serverUrl}/handshake`;

    let msgCount = 0;
    const params = {
        tags: {name: 'WebSocketSubscriber'},
        timeout: '120s',
    };

    let connectionSuccessful = false;
    let subscribed = false;

    try {
        const response = ws.connect(wsEndpoint, params, (socket) => {
            const connectFrame = buildStompConnectFrame(userData, accessToken);
            socket.send(connectFrame);

            socket.on('open', () => {
                connectionSuccessful = true;
                wsConnectSuccess.add(1);
                wsActiveConnections.add(1);
            });

            socket.on('message', (data) => {
                if (data.includes('CONNECTED') && !subscribed) {
                    subscribed = true;
                    const subscribeFrame = buildStompSubscribeFrame(userIndex);
                    socket.send(subscribeFrame);
                } else if (data.includes('ERROR')) {
                    wsConnectFailure.add(1);
                    console.error(`WebSocket error for ${userData.username}: ${data}`);
                } else if (data.includes('MESSAGE')) {
                    msgCount++;
                    messagesReceived.add(1);

                    // Extract timestamp for E2E latency calculation
                    const match = data.match(/perf_ts_(\d+)/);
                    if (match) {
                        const sentTime = parseInt(match[1], 10);
                        e2eLatency.add(Date.now() - sentTime);
                    }
                }
            });

            socket.on('close', () => {
                if (connectionSuccessful) {
                    wsActiveConnections.add(-1);
                }
            });

            socket.on('error', (e) => {
                wsConnectFailure.add(1);
            });

            socket.setTimeout(() => {
                messagesPerUser.add(msgCount);
                if (msgCount === 0) {
                    fanoutErrors.add(1);
                }
                socket.close();
            }, 110000);
        });

        if (!connectionSuccessful) {
            wsConnectFailure.add(1);
        }

    } catch (e) {
        console.error(`WebSocket connection failed for ${userData.username}: ${e}`);
        wsConnectFailure.add(1);
    }
}

function buildStompConnectFrame(userData, accessToken) {
    return `CONNECT
accept-version:1.0,1.1,1.2
Authorization:Bearer ${accessToken}
heart-beat:0,0

\0`;
}

function buildStompSubscribeFrame(userIndex) {
    const subscriptionId = `user-queue-${userIndex}`;
    return `SUBSCRIBE
id:${subscriptionId}
destination:/user/queue

\0`;
}

function publishMessageViaREST(userIndex, userData, accessToken) {
    const serverUrl = getServerByRoundRobin(userIndex, false);
    const postingId = crypto.randomUUID();
    const endpoint = `${serverUrl}/chats/${CHAT_ID}/texts/${postingId}`;

    // Embed timestamp for E2E tracking
    const payload = {
        content: `perf_ts_${Date.now()}`
    };

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        timeout: '30s',
    };


    try {
        const response = http.put(endpoint, JSON.stringify(payload), params);

        const success = check(response, {
            'Message published': (r) => r.status === 202 || r.status === 200,
        });

        if (success) {
            restApiSuccess.add(1);
        } else {
            restApiFailure.add(1);
        }

    } catch (e) {
        console.error(`Failed to publish message for ${userData.username}: ${e}`);
        restApiFailure.add(1);
    }
}


