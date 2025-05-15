## Spring Boot Chat Application

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, and participate in live chat rooms.

### Tech Stack

* **Backend:** Spring Boot, Spring Security, Spring Data JPA, WebSocket
* **Database:** MySQL
* **Cache:** Redis
* **Build Tool:** Maven
* **Containerization:** Docker, Docker Compose

### Repositories

* Backend: [https://github.com/teri1712/spring-boot-chat-app.git](https://github.com/teri1712/spring-boot-chat-app.git)
*
Frontend: [https://github.com/teri1712/angular-chat-application.git](https://github.com/teri1712/angular-chat-application.git)

### Prerequisites

* Java 17
* Maven 3.x
* Docker & Docker Compose

### Setup & Run

```bash
# 1. Package the backend (skip tests)
mvn clean package -Dmaven.test.skip=true

# 2. Launch services via Docker Compose
docker-compose up --build
```

### Accessing the Application

* **Profile Management (Backend)**: [http://localhost:8080/](http://localhost:8080/)
* **Chat Application (Frontend)**: [http://localhost:4200/](http://localhost:4200/)

Refer to the frontend repository for detailed Angular setup and configuration.
