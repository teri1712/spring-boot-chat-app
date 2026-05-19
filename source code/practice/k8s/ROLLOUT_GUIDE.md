# The Three-Tier Professional Helm Rollout Guide

This guide follows the **Lifecycle-Based** architecture. Each tier has a different purpose and is updated at a different frequency.

---

## Tier 1: Observability (The "Eyes")
**Frequency:** Once per project setup / month.
**Purpose:** Loki, Grafana, Tempo, and Prometheus.

```bash
cd k8s/observability
helm dependency update
helm upgrade --install obs . --namespace monitoring --create-namespace
cd ../..
```

---

## Tier 2: Infrastructure (The "Bones")
**Frequency:** Once per week / month.
**Purpose:** Databases (Postgres, Redis) and Messaging (Kafka).

```bash
cd k8s/infra
helm dependency update
helm upgrade --install infra . -f values-local.yaml --namespace dev --create-namespace
cd ../..
```

---

## Tier 3: Application (The "Brain")
**Frequency:** Every 5–10 minutes during development.
**Purpose:** Your Java server code.

```bash
# 1. Update tag
export IMAGE_TAG=$(date +%s)

# 2. Build & Load (Simulated CI/CD)
./mvnw jib:dockerBuild -Dimage=teri1712/chat-app:${IMAGE_TAG}
kind load docker-image teri1712/chat-app:${IMAGE_TAG} --name messenger-labs

# 3. Deploy App ONLY (Blazing Fast)
helm upgrade --install messenger ./k8s/chatapp \
  -f ./k8s/chatapp/values-local.yaml \
  --set image.tag=${IMAGE_TAG} \
  --namespace dev
```

---

## Why this is the Best Practice:
1.  **Safety:** If your Java app crashes and you `helm uninstall messenger`, you **DO NOT** lose your logs in Loki or your data in Postgres.
2.  **Speed:** Tier 3 rollouts take ~10 seconds because Helm doesn't have to check the health of Kafka or Prometheus.
3.  **Isolation:** You can experiment with new Prometheus settings in Tier 1 without any risk of accidentally restarting your Database in Tier 2.
