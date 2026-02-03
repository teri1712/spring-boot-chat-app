## Spring Boot Chat Application

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, and participate in live chat rooms.

## Live web app

https://angular-chat-application-psi.vercel.app (Unavailable now due to budget constraints)

## Demo
A initial version of the demo is available here:

* Web: [https://youtu.be/xm_pWF36_Uo](https://youtu.be/xm_pWF36_Uo)
* Android: [https://youtu.be/E1SQVj2nTtw](https://youtu.be/E1SQVj2nTtw)

### Tech Stack

* **Backend:** Spring Boot, Spring Security, Hibernate, WebSocket
* **Database:** POSTGRES
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

or development deployment with:

```bash
cd "source code/practice"
docker-compose -f docker-compose.dev.yml up -d --build
mvn spring-boot:run
```

## Refer to the frontend repository for detailed Angular setup and configuration.

## Reports

### Query Cache
- Please refer to the `perf_report` for more details.

### Test report
- Please refer to the `test_report` for more details.