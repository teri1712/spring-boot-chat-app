import http from 'k6/http';
import {check, group} from 'k6';
import {Trend} from 'k6/metrics';

const jwtData = JSON.parse(open('./jwts.json'));
const userIds = Object.values(jwtData).map(jwt => {
    try {
        const profile = jwt.profile;
        return profile.id;
    } catch (e) {
        console.error('Failed to parse JWT', e);
        return null;
    }
}).filter(id => id !== null);

// Custom metric for tracking response times
const requestDuration = new Trend('user_info_duration');

// Get server host/port from environment variable (e.g. localhost:8080)
const SERVER_URL = __ENV.SERVER_URL || 'localhost:8080';

const config = {
    userIdsPerRequest: 10,
};

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

// Helper function to get random user IDs
function getRandomUserIds(count) {
    const selected = [];
    for (let i = 0; i < count; i++) {
        selected.push(userIds[Math.floor(Math.random() * userIds.length)]);
    }
    return selected;
}

function makeUserRequest(accessToken, userIdList) {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        tags: {name: 'GetUsers'},
    };

    const queryString = userIdList.map(id => `userId=${id}`).join('&');
    const fullUrl = `http://${SERVER_URL}/infos?${queryString}`;

    const response = http.get(fullUrl, params);

    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    if (response.status !== 200) {
        console.error(`Request to ${fullUrl} failed with status ${response.status}`);
    }

    requestDuration.add(response.timings.duration);

    return response;
}

export default function () {
    const randomUserIds = getRandomUserIds(config.userIdsPerRequest);

    group('User Info Request', function () {
        // Using user_1 as a standard requester
        makeUserRequest(jwtData["user_1"].token, randomUserIds);
    });
}
