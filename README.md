## Chat Application following Hexagonal and Modulith architectures

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, searching and participate in live chat rooms.

## Live web app

https://t1ichat-frontend-staging-732519168410.asia-southeast1.run.app/

## Demo

A initial version of the application demo is available here:

* Web: [https://youtu.be/xm_pWF36_Uo](https://youtu.be/xm_pWF36_Uo)
* Android: [https://youtu.be/E1SQVj2nTtw](https://youtu.be/E1SQVj2nTtw)

### Tech Stack

* **Backend:** Spring Boot, Spring Security, Hibernate, WebSocket
* **Database:** Postgres
* **Cache:** Redis
* **Build Tool:** Maven
* **Containerization:** Docker, Docker Compose

### Repositories

* Backend: [https://github.com/teri1712/spring-boot-chat-app.git](https://github.com/teri1712/spring-boot-chat-app.git)
*
Frontend: [https://github.com/teri1712/angular-chat-application.git](https://github.com/teri1712/angular-chat-application.git)

## Run Locally

This repository contains the Spring Boot backend. You can run it with Docker Compose.

### Prerequisites

* Java 17
* Maven 3.x
* Docker & Docker Compose 2.x

### Local Environment Deployments with Docker Compose

From the repository root:

```bash
docker compose up -d
mvn spring-boot:run
```

Visit http://localhost:4200/ to access the application.

# Performance Test Report

- Detailed reports and scripts are available in the [performance/](./performance/) directory.

## Cache Performance Test (Large Room Scenario)

### Test Setup

- **Scenario**: Fetching chat logs for a room with 5,000+ messages and 500 participants.
- **Load Profile**:
    - 0-30s: Ramp up to 20 concurrent users
    - 30s-90s: Maintain 50 concurrent users
    - 90s-120s: Ramp down to 0
- **Metrics Source**: `performance/cache/no-cache.json` and `performance/cache/with-cache.json`

### Results

| Metric            | Cache Disabled | Cache Enabled | Improvement |
|-------------------|----------------|---------------|-------------|
| Avg Response Time | 530.42 ms      | 368.59 ms     | **~30.5%**  |
| Throughput (Reqs) | 5,928          | 8,537         | **~44.0%**  |
| Success Rate      | 100%           | 100%          | -           |

---

## Resilience & Throughput (Kafka vs Internal Events)

### Test Setup
- **Scenario**: High-volume messaging fanout to 500 concurrent subscribers.
- **Load Profile**: Constant arrival rate of 30 messages/sec.
- **Infrastructure**: Distributed staging cluster.
- **Goal**: Compare system resilience and publishing throughput between internal module events and Kafka-backed messaging.
- **Metrics Source**: `performance/resilience/module-batch-fanout.json` and `performance/resilience/kafka-batch-fanout.json`

### Results

| Metric | Internal Events | Kafka Batching | Improvement |
|--------|-----------------|----------------|-------------|
| Successful Publishes | ~0.73 msgs/sec | **~4.40 msgs/sec** | **~6.0x Higher** |
| Success Rate | ~6.1% | **~34.3%** | **~5.6x Higher** |
| Reliability | Highly sensitive to DB load | **Buffered & resilient** | - |

---

# Test Report

The project maintains high code quality with a comprehensive test suite. We have achieved **over 85% test coverage**.

- Detailed JaCoCo coverage reports are available in: https://codecov.io/gh/teri1712/spring-boot-chat-app
