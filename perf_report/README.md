# 1. No cache commands

mvn spring-boot:run -Dspring-boot.run.arguments="--server.cache.events=false --outbox.scheduling.enabled=false"

# 2. Has cache commands

mvn spring-boot:run -Dspring-boot.run.arguments="--server.cache.events=true --outbox.scheduling.enabled=false"
