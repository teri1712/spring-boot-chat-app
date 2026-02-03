## Spring Boot Chat Application

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, searching and participate in live chat rooms.

## Live web app

https://angular-chat-application-psi.vercel.app (Unavailable now due to budget constraints)

## Demo
A initial version of the application is available here:

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
docker-compose -f docker-compose.deploy.yml up -d --build
```

or development deployable services with:

```bash
cd "source code/practice"
docker-compose -f docker-compose.dev.yml up -d --build

mvn spring-boot:run
```

## Refer to the frontend repository for detailed Angular setup and configuration.

## Reports

### Performance Report

This section compares the performance of the application with and without event caching. The benchmarks were conducted using JMeter.

#### No Cache
| Label | # Samples | Average (ms) | Min (ms) | Max (ms) | Throughput |
|-------|-----------|--------------|----------|----------|------------|
| my messages | 5000 | 8 | 5 | 210 | 237.6/sec |
| chat messages | 5000 | 9 | 5 | 52 | 240.0/sec |
| **TOTAL** | **10000** | **9** | **5** | **210** | **475.1/sec** |

#### With Cache (Redis)
| Label | # Samples | Average (ms) | Min (ms) | Max (ms) | Throughput |
|-------|-----------|--------------|----------|----------|------------|
| my messages | 5000 | 3 | 2 | 198 | 246.4/sec |
| chat messages | 5000 | 3 | 2 | 43 | 248.8/sec |
| **TOTAL** | **10000** | **3** | **2** | **198** | **492.7/sec** |

*Note: Enabling cache reduced average latency by approximately 66%. Detailed CSV reports can be found in the `perf_report` folder.*

### Test Report

The project maintains high code quality with a comprehensive test suite. We have achieved **over 80% test coverage**.

- Detailed JaCoCo coverage reports are available in: `test_report/site/jacoco`
- To view the report, open `test_report/site/jacoco/index.html` in your browser.