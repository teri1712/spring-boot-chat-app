import ws from 'k6/ws';
import http from 'k6/http';
import {check, group} from 'k6';
import {Counter, Gauge, Trend} from 'k6/metrics';

const jwts = JSON.parse(open("./jwts.json"));

const NUM_USERS = 100;
const NUM_SERVERS = 5;
const CHAT_ID = 'group_5k_perf_test';

// Server URLs - Update these with your actual server addresses
const SERVER_URLS = [
    'ws://localhost:8081',
    'ws://localhost:8082',
    'ws://localhost:8083',
    'ws://localhost:8084',
    'ws://localhost:8085'
];

const REST_URLS = [
    'http://localhost:8081',
    'http://localhost:8082',
    'http://localhost:8083',
    'http://localhost:8084',
    'http://localhost:8085'
];
const wsConnectSuccess = new Counter('ws_connect_success');
const wsConnectFailure = new Counter('ws_connect_failure');
const wsActiveConnections = new Gauge('ws_active_connections');

const messagesReceived = new Counter('messages_received');
const messagesPerUser = new Trend('messages_per_user');
const fanoutErrors = new Counter('fanout_errors');

const restApiSuccess = new Counter('rest_api_success');
const restApiFailure = new Counter('rest_api_failure');

export const options = {
    scenarios: {
        subscribers: {
            executor: 'constant-vus',
            vus: 100,
            duration: '2m',
            exec: 'subscriberScenario',
        },
        publishers: {
            executor: 'constant-arrival-rate',
            rate: 30,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 30,
            maxVUs: 100,
            exec: 'publisherScenario',
        },
    },
    thresholds: {
        'ws_connect_success': ['rate > 0.95'],
        'messages_received': ['count > 20000'],
        'messages_per_user': ['p(95) > 300'],
    },
};


function generateUserData(userIndex) {
    return {
        username: `user_${String(userIndex + 1)}`,
        name: `Performance User ${userIndex + 1}`
    };
}

function getServerByRoundRobin(userIndex, isWebSocket = true) {
    const serverIndex = userIndex % NUM_SERVERS;
    return isWebSocket ? SERVER_URLS[serverIndex] : REST_URLS[serverIndex];
}

export function subscriberScenario() {
    const vuId = __VU;
    const userIndex = (vuId - 1) % NUM_USERS;

    const userData = generateUserData(userIndex);

    // Login to get real token
    let accessToken = '';
    group('Login', () => {
        accessToken = login(userData);
    });

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

    let accessToken = '';
    group('Login', () => {
        accessToken = login(userData);
    });

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
                    console.error(`No message received by ${userData.username}`);
                    fanoutErrors.add(1);
                }
                socket.close();
            }, 120000);
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

    const payload = {
        content: `Message from ${userData.username} at ${new Date().toISOString()}`
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

        check(response, {
            'Message published': (r) => r.status === 202 || r.status === 200,
        });
        restApiSuccess.add(1);

    } catch (e) {
        console.error(`Failed to publish message for ${userData.username}: ${e}`);
        restApiFailure.add(1);
    }
}


