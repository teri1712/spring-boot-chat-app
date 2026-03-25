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

// Custom metrics for tracking response times
const cacheEnabledDuration = new Trend('cache_enabled_duration');
const cacheDisabledDuration = new Trend('cache_disabled_duration');

const config = {
    // Pool 1: Cache disabled (2 servers)
    cacheDisabledPool: [
        'http://localhost:8081',
        'http://localhost:8082',
    ],
    // Pool 2: Cache enabled (2 servers)
    cacheEnabledPool: [
        'http://localhost:8083',
        'http://localhost:8084',
    ],
    userIdsPerRequest: 10,
};

// Round-robin counters for each pool
let cacheDisabledCounter = 0;
let cacheEnabledCounter = 0;

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

function getServerFromPool(pool, counterKey) {
    const serverUrl = pool[counterKey % pool.length];
    return serverUrl;
}

// Helper function to get random user IDs
function getRandomUserIds(count) {
    const selected = [];
    for (let i = 0; i < count; i++) {
        selected.push(userIds[Math.floor(Math.random() * userIds.length)]);
    }
    return selected;
}

function makeUserRequest(accessToken, pool, userIdList, cacheStatus, counterKey) {
    const url = getServerFromPool(pool, counterKey);

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        tags: {name: `GetUsers_${cacheStatus}`},
    };

    let queryString = '';
    userIdList.forEach((userId, index) => {
        queryString += `userId=${userId}`;
        if (index < userIdList.length - 1) {
            queryString += '&';
        }
    });

    const fullUrl = `${url}/user-infos?${queryString}`;
    const response = http.get(fullUrl, params);

    const cacheLabel = cacheStatus === 'CacheEnabled' ? 'with cache' : 'without cache';
    check(response, {
        [`status is 200 (${cacheLabel})`]: (r) => r.status === 200,
        [`response time < 500ms (${cacheLabel})`]: (r) => r.timings.duration < 500,
    });
    const success = response.status === 200;
    if (!success)
        console.error(`Request to ${fullUrl} failed with status ${response.status} (${cacheLabel})`);

    // Track metrics for each cache scenario
    if (cacheStatus === 'CacheEnabled') {
        cacheEnabledDuration.add(response.timings.duration);
    } else {
        cacheDisabledDuration.add(response.timings.duration);
    }

    return response;
}

export default function () {
    const randomUserIds = getRandomUserIds(config.userIdsPerRequest);

    group('Cache Disabled Pool', function () {
        makeUserRequest(jwtData["user_1"].token, config.cacheDisabledPool, randomUserIds, 'CacheDisabled', cacheDisabledCounter++);
    });

    group('Cache Enabled Pool', function () {
        makeUserRequest(jwtData["user_1"].token, config.cacheEnabledPool, randomUserIds, 'CacheEnabled', cacheEnabledCounter++);
    });

}


