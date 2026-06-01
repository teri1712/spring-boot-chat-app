---
name: infra-setup-postgres
description: Set up PostgreSQL for the project, covering Docker Compose, Helm (Kubernetes), and Spring Boot binding. Use when setting up PostgreSQL, adding a new database, or configuring persistence in Spring Boot.
---

# Infra Setup: PostgreSQL

## Quick start

### 1. Update `compose.yaml`
Add the postgres service:
```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: chatapp
      POSTGRES_USER: root
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d chatapp" ]
      interval: 10s
      timeout: 5s
      retries: 5
```

### 2. Spring Boot Binding (Mandatory)
Update the following properties:

**`src/main/resources/application.yml`**:
```yaml
spring:
  docker:
    compose:
      enabled: true
      lifecycle-management: start_only
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

**`src/main/resources/application-dev.yml`**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chatapp
    username: root
    password: root
```

**`src/main/resources/application-prod.yml`**:
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### 3. Flyway Configuration (Maven)
Update `pom.xml` to include the Flyway plugin for local development:
```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/chatapp</url>
        <user>root</user>
        <password>root</password>
        <cleanDisabled>false</cleanDisabled>
    </configuration>
</plugin>
```

### 4. Kubernetes (Helm) & Release
Update `k8s/infra/Chart.yaml` dependencies:
```yaml
dependencies:
  - name: postgresql
    version: 18.6.7
    repository: https://charts.bitnami.com/bitnami
```

**Local Configuration (`k8s/infra/values-local.yaml`):**
```yaml
postgresql:
  primary:
    persistence:
      enabled: false # Scale down for local Kind cluster
```

**Production Configuration (`k8s/infra/values-prod.yaml`):**
```yaml
postgresql:
  primary:
    persistence:
      storageClass: "premium-rwo"
      size: 10Gi
    resources:
      limits:
        cpu: 1000m
        memory: 2Gi
      requests:
        cpu: 500m
        memory: 1Gi
```

**Deployment / Release:**
1. Run `helm dependency update k8s/infra`.
2. Ensure the secret `chatapp-secrets` exists in the target namespace.
3. Deploy Local: `helm upgrade --install infra k8s/infra -f k8s/infra/values-local.yaml`
4. Deploy Prod: `helm upgrade --install infra k8s/infra -f k8s/infra/values-prod.yaml`

## Advanced features

- **Migrations:** Use Flyway. Place SQL files in `src/main/resources/db/migration/`.
  - Use `V<Number>__<Description>.sql` for versioned migrations.
  - Use `V<Number>__seed_<Table>.sql` for initial data seeding.
- **Indexing:** Follow naming conventions like `idx_<table_name>_<column_name>`.
  - Always verify execution plans with `EXPLAIN ANALYZE` for complex queries.
- **Performance:** For high-throughput tables, consider using `BRIN` indexes or partitioning if the table grows beyond 100M rows.
- **Data Integrity:** Use check constraints and foreign keys to enforce business rules at the database level.
