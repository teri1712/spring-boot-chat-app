## Set up services

```bash
cd "source code/practice"
docker-compose -f docker-compose.dev.yml up -d --build
```

## No cache commands
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.cache.events=false --outbox.scheduling.enabled=false"
```

## Has cache commands
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.cache.events=true --outbox.scheduling.enabled=false"
```