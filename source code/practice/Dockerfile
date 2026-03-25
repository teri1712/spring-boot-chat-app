FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

RUN apk --no-cache add curl

COPY --from=builder /app/target/chat-app-server-0.0.1-SNAPSHOT.jar server.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java","-jar","server.jar"]

