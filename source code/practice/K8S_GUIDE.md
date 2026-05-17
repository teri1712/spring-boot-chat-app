# The Ultimate Kubernetes & GKE Master Guide: Messenger Clone (Final Edition)

This guide provides **production-grade** code comparing **Conventional K8s (Raw YAML)** with **Helm (Templated YAML)** for every environment.

---

## 1. Fundamentals: The K8s Mental Model

| Concept | Conventional K8s (The "Why") | Helm (The "How") |
| :--- | :--- | :--- |
| **Pod** | Smallest unit (one or more containers). Ephemeral. | Defined in `templates/deployment.yaml`. |
| **Service** | Permanent DNS/IP address to reach Pods. | Defined in `templates/service.yaml`. |
| **Deployment** | Manager that ensures the "Desired State" (replica count). | The core template of your application. |
| **StatefulSet** | Manager for Databases (Postgres/Redis). | Used by Bitnami sub-charts for infra. |
| **PVC** | A request for a real physical disk (Persistent Volume). | Managed via `volumeClaimTemplates`. |
| **HPA** | The "Auto-scaler" (Horizontal Pod Autoscaler). | Defined in `templates/hpa.yaml`. |

---

## 2. Secret Management: Two Professional Paths

### Path A: Manual Pre-creation (Safer & Recommended)
You run the `kubectl create secret` command **once** on each cluster.
*   **Local (Kind)**: `kubectl create secret generic messenger-secrets --from-literal=postgres-password=root`
*   **Prod (GKE)**: `kubectl create secret generic messenger-secrets --from-literal=postgres-password=XyZ_PROD_PASS`

---

## 3. Step-by-Step Implementation: Raw YAML vs. Helm

### Step 1: Infrastructure (PostgreSQL with Persistence)
**Conventional K8s (Raw YAML)**:
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: "postgres"
  replicas: 1
  template:
    spec:
      containers:
      - name: postgres
        image: postgres:16
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 10Gi
```

**Helm (The Professional Way)**:
Add to `Chart.yaml`:
```yaml
dependencies:
  - name: postgresql
    version: 15.5.21
    repository: https://charts.bitnami.com/bitnami
```

**Tuning in `values.yaml`**:
```yaml
postgresql:
  primary:
    resources:
      limits: { cpu: 500m, memory: 1Gi }
    persistence:
      size: 20Gi
      storageClass: "premium-rwo"
    extendedConfiguration: |
      max_connections = 200
```

### Step 2: Application Deployment
**Helm Template (`templates/deployment.yaml`)**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "messenger-server.fullname" . }}
spec:
  # PRO-TIP: Only set replicas if auto-scaling is OFF.
  {{- if not .Values.autoscaling.enabled }}
  replicas: {{ .Values.replicaCount }}
  {{- end }}
  template:
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        resources:
          {{- toYaml .Values.resources | nindent 10 }}
        env:
        - name: SPRING_DATASOURCE_URL
          value: {{ .Values.env.DATABASE_URL | quote }}
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: messenger-secrets
              key: postgres-password
```

---

## 4. Multi-Environment Strategy (Base + Override)

| File | Context | Typical Overrides |
| :--- | :--- | :--- |
| `values.yaml` | Base Defaults | Image Repo, Port 8081. |
| `values-local.yaml` | Kind/Local | `replicaCount: 1`, `autoscaling: enabled: false`. |
| `values-prod.yaml` | GKE/Prod | `replicaCount: 3`, `autoscaling: enabled: true`. |

---

## 5. Scaling & Auto-scaling (The "Core Feature")

### The "Helm vs. HPA" Conflict
In production, if you use **HPA**, you must remove `replicas:` from your deployment file. 
- **The Solution**: Use the `{{- if not .Values.autoscaling.enabled }}` logic in your deployment template.

### Helm HPA Template (`templates/hpa.yaml`)
```yaml
{{- if .Values.autoscaling.enabled }}
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: {{ include "messenger-server.fullname" . }}
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: {{ include "messenger-server.fullname" . }}
  minReplicas: {{ .Values.autoscaling.minReplicas }}
  maxReplicas: {{ .Values.autoscaling.maxReplicas }}
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: {{ .Values.autoscaling.targetCPUUtilizationPercentage }}
{{- end }}
```

---

## 6. The Observability Stack (LGTM) - Explicit Implementation

In Kubernetes, we use **Operators** to manage monitoring. Follow these explicit steps:

### Step 1: Add Repos & Install Stack
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

# Install Prometheus + Grafana
helm install obs prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace

# Install Loki (Logs) & Tempo (Traces)
helm install loki grafana/loki-stack --namespace monitoring
helm install tempo grafana/tempo --namespace monitoring
```

### Step 2: Connect your App (ServiceMonitor)
Create this file in your `templates/` folder. This is how Prometheus "finds" your Spring Boot metrics.

**`templates/servicemonitor.yaml`**
```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: {{ include "messenger-server.fullname" . }}
  labels:
    release: obs # Must match the 'helm install' name above
spec:
  selector:
    matchLabels:
      {{- include "messenger-server.selectorLabels" . | nindent 6 }}
  endpoints:
  - port: http # Must match the port name in your Service.yaml
    path: /actuator/prometheus
    interval: 15s
```

### Step 3: Accessing Grafana & Connecting your App
1. **Forward**: `kubectl port-forward svc/obs-grafana 3000:80 -n monitoring`
2. **App Connection (The "Cross-Namespace" Fundamental)**:
   Your app in the `default` namespace finds the monitoring tools using their **Full DNS Name**:

   | Service | App Connection URL |
   | :--- | :--- |
   | **Loki** | `http://loki.monitoring.svc.cluster.local:3100/loki/api/v1/push` |
   | **Tempo** | `http://tempo.monitoring.svc.cluster.local:4317` |

**Pro-Tip**: In K8s, you can talk to any service in any namespace using `[service].[namespace].svc.cluster.local`. This is how your app "finds" the shared monitoring stack!

---

## 7. Local Testing with `kind`

1. **Create**: `kind create cluster --name messenger-labs`
2. **Load Image**: `kind load docker-image messenger-server:latest`.
3. **Connect**: `kubectl port-forward service/messenger-server 8081:8081`.

---

## 8. GKE Production Workflow

1. **Artifact Registry**: `./mvnw jib:build -Dimage=gcr.io/[PROJECT]/messenger-server`.
2. **Ingress**: Use `kubernetes.io/ingress.class: "gce"` to create a Google Cloud Load Balancer.

---

## 9. Helm Templating 101

*   **`{{-`**: Removes extra whitespace.
*   **`| nindent 4`**: Forces correct YAML indentation.
*   **`helm template .`**: Render locally to verify your YAML before deploying.

---

## 10. Dynamic Naming: Why use `fullname`?

The `fullname` helper combines the **Release Name** + **Chart Name** to avoid name collisions.
1. **Local Dev**: `helm install dev .` -> Name becomes `dev-messenger-server`.
2. **Staging**: `helm install staging .` -> Name becomes `staging-messenger-server`.

---

## 11. The Full Stack Architecture: Client + Server + Gateway

### 1. The Gateway (Nginx)
The **Kubernetes Service** handles load balancing automatically. Delete your manual Nginx container.

### 2. Routing: One Ingress to Rule Them All
**`templates/ingress.yaml`**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: messenger-ingress
  annotations:
    kubernetes.io/ingress.class: "gce"
spec:
  rules:
  - http:
      paths:
      - path: /api/*  # To Backend
        backend:
          service: { name: messenger-server, port: { number: 8081 } }
      - path: /*      # To Frontend
        backend:
          service: { name: messenger-client, port: { number: 80 } }
```

---

## 12. Troubleshooting Cheat Sheet

| Command | Purpose |
| :--- | :--- |
| `kubectl get pods -w` | Watch pods state. |
| `kubectl describe pod` | See why a pod is `Pending`. |
| `kubectl logs -f` | Follow live logs. |
| `helm rollback` | Undo a bad deployment. |
