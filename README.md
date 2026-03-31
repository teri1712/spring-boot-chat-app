## Chat Application following Hexagonal and Modulith architectures

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, searching and participate in live chat rooms.

## Live web app
http://decade.ddnsfree.com:4200/
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
* Frontend: [https://github.com/teri1712/angular-chat-application.git](https://github.com/teri1712/angular-chat-application.git)

## Run Locally

This repository contains the Spring Boot backend. You can run it with Docker Compose.

### Prerequisites

* Java 17
* Maven 3.x
* Docker & Docker Compose 2.x

### Local Environment Deployments with Docker Compose

From the repository root:

```bash
cd "source code/practice"
docker network create backend
docker compose -f docker-compose.dev.yml -f docker-compose.infra.yml up -d
```

Visit http://localhost:4200/ to access the application.

# Performance Test Report

- Detailed reports are available in: `performance otimization report`, use compression tools to unzip the files please
- Detailed performance setting up are available ta `source code/practice/perf`

## Cache Performance Test

### Test Setup
- **Scenarios**: 2 server pools compared
  - Pool 1: Cache disabled (2 servers)
  - Pool 2: Cache enabled (2 servers)
- **Load Profile**: 
  - 0-30s: Ramp up to 20 concurrent users
  - 30s-90s: Maintain 50 concurrent users
  - 90s-120s: Ramp down to 0
- **Total Requests**: 34,772



### Results

| Metric            | Value         |
| ----------------- | ------------- |
| Min Response Time | 4.25 ms       |
| Max Response Time | 1,520.00 ms   |
| Avg Response Time | **89.94 ms**  |
| Success Rate      | **99.20%**    |
| Failed Requests   | 0             |

---

### Cache Performance Comparison

| Scenario       | Avg Response Time |
| -------------- | ----------------- |
| Cache Disabled | **128.83 ms**     |
| Cache Enabled  | **51.06 ms**      |

---

### Performance Impact

* Enabling cache reduced average response time by **~60.4%**
* Achieved **~2.52× faster response times** with caching enabled

---

## Fanout Performance Test (WebSocket Messaging)

### Test Setup
- **Scenarios**: Publisher-Subscriber messaging pattern
  - **Subscribers**: 100 concurrent WebSocket connections (constant) to receive fanout
  - **Publishers**: 30 messages/sec (constant arrival rate), with actual success rate at ~5.5 messages/sec due to database concurrent large fanout batch insertions and system constraints as bottleneck.
  - **Test Duration**: 2 minutes per scenario
- **Infrastructure**: 5 server instances for load distribution
- **Total Requests**: 791 REST API calls

### Results
| Metric | Value |
|--------|-------|
| WebSocket Connections | **100 concurrent** |
| Message Delivery Rate | **~11,137 messages/minute** |

---


# Test Report

The project maintains high code quality with a comprehensive test suite. We have achieved **over 85% test coverage**.

- Detailed JaCoCo coverage reports are available in: https://codecov.io/gh/teri1712/spring-boot-chat-app
