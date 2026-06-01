import http from 'k6/http';
import {check, group} from 'k6';
import {Trend} from 'k6/metrics';

const jwtData = JSON.parse(open('./jwts.json'));
const usernames = Object.keys(jwtData);

// Custom metric for tracking response times
const requestDuration = new Trend('chat_logs_duration');

// Get server host/port from environment variable
const SERVER_URL = __ENV.SERVER_URL || 'localhost:8080';
const CHAT_ID = 'group_5k_perf_test';

export const options = {
    stages: [
        {duration: '30s', target: 20},
        {duration: '1m', target: 50},
        {duration: '30s', target: 0},
    ],
    thresholds: {
        'http_req_failed': ['rate<0.01'],
        'http_req_duration': ['p(95)<500'],
    },
};

export default function () {
    // Pick a user for this VU/iteration
    const userIndex = (__VU - 1 + __ITER) % usernames.length;
    const userKey = usernames[userIndex];
    const accessToken = jwtData[userKey].token;

    const params = {
        headers: {
            'Authorization': `Bearer ${accessToken}`,
        },
        tags: {name: 'GetChatLogs'},
    };

    // anchorSequenceNumber as requested (using JS safe max integer)
    const anchorSequenceNumber = 0;
    const fullUrl = `http://${SERVER_URL}/chats/${CHAT_ID}/logs?anchorSequenceNumber=${anchorSequenceNumber}`;

    group('Get Chat Logs', function () {
        const response = http.get(fullUrl, params);

        check(response, {
            'status is 200': (r) => r.status === 200,
        });

        if (response.status !== 200) {
            console.error(`User ${userKey} (VU ${__VU}) failed to get logs from ${fullUrl}. Status: ${response.status}`);
        }

        requestDuration.add(response.timings.duration);
    });
}

[]