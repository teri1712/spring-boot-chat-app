FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY target/spring-boot.jar spring-boot.jar

COPY images/ images/

EXPOSE 8080

ENTRYPOINT ["java","-jar","spring-boot.jar"]
