# Performance Testing Guide

This directory contains load testing scripts and infrastructure setup for the messenger-clone server.

## Prerequisites

- Docker and Docker Compose installed
- k6 load testing tool installed
- Current working directory: `perf/`

## Running Performance Tests

### Step 1: Start Infrastructure

Start the Docker containers with 5 server instances and all supporting services:

```bash
docker compose -f docker-compose.infra.yml -f docker-compose.5instances.yml up -d
```

This will spin up:

- 5 messenger server instances (for load distribution)
- PostgreSQL database
- Redis cache
- Elasticsearch
- LocalStack (S3 mock)
- Filebeat (logging)
- Prometheus (monitoring)

### Step 2: Seed Test Data

Prepare the test environment with seed data and JWT tokens:

```bash
chmod +x ./seed-pipeline.sh
./seed-pipeline.sh
```

This script will:

- Initialize the database schema
- Create test users and chat groups
- Generate JWT tokens for authentication
- Populate test data needed for load tests

### Step 3: Run Performance Tests

#### Cache Performance Test

Run the cache layer performance test:

```bash
k6 run --out json=cache-report.json k6-cache.js
```

This test evaluates:

- Cache-enabled vs cache-disabled server pools
- Response time under ramping concurrent load (0-50 users)
- Cache effectiveness and stability

#### Fanout Performance Test

Run the WebSocket fanout messaging test:

```bash
k6 run --out json=fanout-report.json k6-fanout.js
```

This test evaluates:

- WebSocket pub/sub messaging at scale
- 500 concurrent subscribers with 30 messages/sec publishers
- Message delivery across distributed server instances

### Step 4: Review Results

Test results are saved as JSON files:

- `cache-report.json` - Cache performance metrics
- `fanout-report.json` - WebSocket fanout metrics

View results in k6's dashboard or parse the JSON files for custom analysis.
