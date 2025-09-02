## Spring Boot Chat Application

A real-time chat application built with Spring Boot for the backend and Angular for the frontend. Users can manage
profiles, authenticate, and participate in live chat rooms.

## Live web app

https://angular-chat-application-psi.vercel.app

## Demo
A short version of the demo is available here:

* Android: [https://youtu.be/E1SQVj2nTtw](https://youtu.be/E1SQVj2nTtw)
* Web: [https://youtu.be/xm_pWF36_Uo](https://youtu.be/xm_pWF36_Uo)

### Tech Stack

* **Backend:** Spring Boot, Spring Security, Spring Data JPA, WebSocket
* **Database:** MySQL
* **Cache:** Redis
* **Build Tool:** Maven
* **Containerization:** Docker, Docker Compose

### Repositories

* Backend: [https://github.com/teri1712/spring-boot-chat-app.git](https://github.com/teri1712/spring-boot-chat-app.git)
* Frontend: [https://github.com/teri1712/angular-chat-application.git](https://github.com/teri1712/angular-chat-application.git)

## Run Locally

This repository contains the Spring Boot backend. You can run it either with Docker Compose (recommended) or directly with Maven/Java.

### Prerequisites

* Java 17
* Maven 3.x
* Docker & Docker Compose

### Environment configuration

The backend reads configuration from application.properties. For local development, the defaults under practice/src/main/resources/application.properties are used. You will need running instances of MySQL and Redis and to provide their connection details. If you use Docker Compose below, these services are provisioned for you.

Key properties you may override via environment variables or JVM system properties:

* spring.datasource.url, spring.datasource.username, spring.datasource.password
* spring.data.redis.host, spring.data.redis.port
* frontend.host.address (the Angular app origin, e.g. http://localhost:4200)

### Deploy with Docker Compose (backend + MySQL + Redis)

1) Configure environment in practice/.env (Compose reads this automatically):
- SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI
- SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENTID
- SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENTSECRET
- MYSQL_ROOT_PASSWORD (for the MySQL container)
- MYSQL_DATABASE (default: chatapp)

2) Choose profile and domains:
- Local development: remove or comment `SPRING_PROFILES_ACTIVE=production` in practice/.env (or set it empty). Defaults from application.properties will be used:
  - Backend: http://localhost:8080
  - Frontend origin: http://localhost:4200
  - MySQL: localhost:3306 (or the compose mysql service)
  - Redis: localhost:6379 (or the compose redis service)
- Production: keep `SPRING_PROFILES_ACTIVE=production` and edit practice/src/main/resources/application-production.properties to your domains:
  - frontend.host.address = https://YOUR_FRONTEND_DOMAIN
  - app.host.address = https://YOUR_BACKEND_DOMAIN
  Review cookie/security settings there if needed.

From the repository root (where docker-compose.yml is located under practice/):

```bash
cd practice
docker-compose up --build
```
## Refer to the frontend repository for detailed Angular setup and configuration.
