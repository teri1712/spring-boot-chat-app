## Spring Boot Chat Application

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, searching and participate in live chat rooms.

## Live web app

https://angular-chat-application-psi.vercel.app (Unavailable now due to budget constraints)

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
docker compose -f docker-compose.dev.yml -f docker-compose.deploy.yml up -d
```

or development deployable services with:

```bash
cd "source code/practice"
docker-compose -f docker-compose.dev.yml up -d

mvn spring-boot:run
```

## Refer to the frontend repository for detailed Angular setup and configuration.

# Performance Test Report

- Detailed reports are available in: `performance otimization report`
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
- **Total Requests**: 29,388

### Results
| Metric | Value |
|--------|-------|
| Min Response Time | 4.52 ms |
| Max Response Time | 1,168.95 ms |
| Avg Response Time | **106.34 ms** |
| Success Rate | **99.99%** |
| Failed Requests | 0 |

---

## Fanout Performance Test (WebSocket Messaging)

### Test Setup
- **Scenarios**: Publisher-Subscriber messaging pattern
  - **Subscribers**: 500 concurrent WebSocket connections (constant) to receive fanout
  - **Publishers**: 30 messages/sec (constant arrival rate)
  - **Test Duration**: 2 minutes per scenario
- **Infrastructure**: 5 server instances for load distribution
- **Total Requests**: 618 REST API calls

### Results
| Metric | Value |
|--------|-------|
| Avg Response Time | **20,922.68 ms** |
| WebSocket Connections | **500 concurrent** |
| Message Delivery Rate | **30 messages/sec** |


### Test Report

The project maintains high code quality with a comprehensive test suite. We have achieved **over 80% test coverage**.

- Detailed JaCoCo coverage reports are available in: `test report/jacoco`
- To view the report, open `test report/jacoco/index.html` in your browser.