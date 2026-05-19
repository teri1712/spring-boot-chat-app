# The Ultimate Kubernetes & GKE Master Guide: Messenger Clone (Beginner to Pro)

This guide provides **production-grade** steps to deploy the Messenger Clone application. Since you are new to
Kubernetes (K8s) and Helm, we will explain not just the **how**, but the **why** behind every step.

---

## 1. Prerequisites: Your Toolbelt (Installation Guide)

Before you can run any K8s commands, you must install these tools. Open your terminal and run the commands for your
Operating System:

### A. Docker Desktop (Required First)

You must have Docker running.

* **Linux**: [Install Docker Engine](https://docs.docker.com/engine/install/ubuntu/)
* **macOS/Windows**: Download [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### B. kubectl (The K8s Controller)

* **Linux**:
  ```bash
  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
  sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
  ```
* **macOS**: `brew install kubectl`
* **Windows**: `curl.exe -LO "https://dl.k8s.io/release/v1.29.0/bin/windows/amd64/kubectl.exe"`

### C. Helm (The Package Manager)

* **Linux/macOS**:
  ```bash
  curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
  ```
* **Windows**: `choco install kubernetes-helm` (using Chocolatey)

### D. kind (Local K8s Cluster)

* **Linux**:
  ```bash
  [ $(uname -m) = x86_64 ] && curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.22.0/kind-linux-amd64
  chmod +x ./kind
  sudo mv ./kind /usr/local/bin/kind
  ```
* **macOS**: `brew install kind`
* **Windows**: `curl.exe -Lo kind-windows-amd64.exe https://kind.sigs.k8s.io/dl/v0.22.0/kind-windows-amd64.exe`

### E. Google Cloud SDK (For GKE)

* **Linux**:
  ```bash
  curl https://sdk.cloud.google.com | bash
  exec -l $SHELL
  gcloud init
  ```
* **macOS/Windows**: Follow the [official installer guide](https://cloud.google.com/sdk/docs/install).

---

---

## 2. Fundamental Concepts (The "Why")

### Q1: Why do I have to "load image" to kind?

... (omitted for brevity)

### Q2: How do I "tell" K8s which cluster to use?

... (omitted for brevity)

### Q3: How do services find each other? (Hostnames)

When you install a Helm chart with dependencies (like Postgres), Kubernetes creates a **Service** with a predictable DNS
name.

* **The Formula**: `[Release-Name]-[Dependency-Name]`
* **Example**: If you run `helm install messenger .`, K8s creates:
    * `messenger-postgresql` (for Postgres)
    * `messenger-redis-master` (for Redis)
    * `messenger-kafka` (for Kafka)
* **The Result**: Inside your Java app, you don't use `localhost`. You use these names as the "host". K8s handles the
  routing automatically.

### Q4: Why are passwords in `values.yaml`? (Security)

In your local `kind` cluster, we put "root/root" in `values.yaml` for **Zero-Config Simplicity** so you can start
learning immediately.

* **Local (Insecure)**: Hardcoded in `values.yaml`. Fine for learning, never for the real world.
* **Production (Secure)**: We use `auth.existingSecret` to tell the databases "Go look in the `chatapp-secrets` vault
  for your password."

---

## 3. Step 0: Initializing the Helm Chart (The Setup)

Before you can deploy, you need to create the "Blueprint" (the Helm Chart). Here is how we built the `k8s/chatapp`
folder:

1. **Generate the Scaffold**:
   ```bash
   mkdir k8s && cd k8s
   helm create chatapp
   ```
   This creates a folder with these key files:
    * **`Chart.yaml`**: The "Identity Card" of your app (Name, version, dependencies).
    * **`values.yaml`**: The "Configuration Panel" (Where you put your database URLs, image tags, etc.).
    * **`templates/`**: The "Blueprints" (YAML files with holes like `{{ .Values.image }}` that Helm fills in).

2. **Add Infrastructure (The "Engines")**:
   Our app needs Postgres, Redis, and Kafka. We don't write these from scratch; we add them as "Dependencies" in
   `Chart.yaml`:
   ```yaml
   dependencies:
     - name: postgresql
       version: 15.5.21
       repository: https://charts.bitnami.com/bitnami
     - name: redis
       version: 19.6.1
       repository: https://charts.bitnami.com/bitnami
   ```

3. **Download the Dependencies**:
   Run this to actually download the code for those databases into your project:
   ```bash
   helm dependency update chatapp/
   ```

4. **Connect the App to the Databases**:
   In `templates/deployment.yaml`, we added the "Bridge" (Environment Variables) so your Java code knows where to find
   Postgres and Kafka. (I have already done this in your project files!)

---

## 4. Step 1: Build the Application Image

We use **Jib** because it's faster and doesn't require writing a Dockerfile.

```bash
# This builds the image and puts it in your HOST machine's Docker list
./mvnw jib:dockerBuild -Dimage=teri1712/chat-app:latest
```

---

## 4. Step 2: Setup Local Cluster (kind)

1. **Create the Cluster**:
   ```bash
   kind create cluster --name messenger-labs
   ```
   *Note: This automatically "tells" kubectl to use this cluster.*

2. **Verify the Connection**:
   ```bash
   kubectl cluster-info --context kind-messenger-labs
   ```

3. **Bridge the Image**:
   ```bash
   # Copy the image from your host Docker into the kind cluster
   kind load docker-image teri1712/chat-app:latest --name messenger-labs
   ```

4. **Create Secrets (Manual Step)**:
   K8s needs a "vault" for your sensitive data. We create a `Secret` object that the app will read.
   ```bash
   kubectl create secret generic chatapp-secrets \
     --from-literal=postgres-password=root \
     --from-literal=jwt-secret=vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl \
     --from-literal=aws-access-id=decade \
     --from-literal=aws-secret-key=decade
   ```

---

## 5. Step 3: Deploy with Helm

Helm uses "Charts" (folders with templates) to deploy everything at once.

1. **Update Dependencies**:
   Our app needs Postgres, Redis, and Kafka. Helm can download these pre-made "sub-charts".
   ```bash
   cd k8s/chatapp
   helm dependency update
   ```

2. **Install the App**:
   We use `values-local.yaml` to tell Helm "use local settings" (like no persistence for DBs to save speed).
   ```bash
   # We install into a 'namespace' called 'dev' to keep things organized
   helm install messenger . -f values-local.yaml --namespace dev --create-namespace
   ```

---

## 6. Step 4: Verification (Did it work?)

1. **Check Pods**:
   ```bash
   kubectl get pods -n dev
   ```
   *Wait until "STATUS" is "Running". It might take 1-2 minutes for Kafka/Postgres to start.*

2. **Access the App (Port Forwarding)**:
   K8s services are private by default. To access it from your browser:
   ```bash
   kubectl port-forward svc/chatapp 8080:80 -n dev
   ```
   Now open: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

3. **See App Logs**:
   If it's failing, look at the "brain" of the app:
   ```bash
   kubectl logs -f deployment/chatapp -n dev
   ```

---

## 7. Step 5: Moving to Production (GKE)

Once you are happy locally, follow these steps for the cloud:

1. **Login to Google Cloud**:
   ```bash
   gcloud auth login
   gcloud config set project [YOUR_PROJECT_ID]
   ```

2. **Connect kubectl to GKE**:
   ```bash
   gcloud container clusters get-credentials [CLUSTER_NAME] --region [REGION]
   ```
   *Note: This "tells" K8s to stop looking at kind and start looking at Google Cloud.*

3. **Push Image to Google Registry**:
   ```bash
   ./mvnw jib:build -Dimage=gcr.io/[PROJECT_ID]/chat-app:latest
   ```

4. **Deploy with Prod Settings**:
   ```bash
   helm install messenger . -f values-gke.yaml --namespace prod --create-namespace
   ```

---

## 8. Troubleshooting for Beginners

| If you see...      | It means...                       | Try this...                                                 |
|:-------------------|:----------------------------------|:------------------------------------------------------------|
| `ErrImagePull`     | K8s can't find your image.        | Did you run `kind load docker-image`?                       |
| `CrashLoopBackOff` | The app started but crashed.      | Run `kubectl logs -n dev [pod_name]` to see the Java error. |
| `Pending` status   | Not enough CPU/Memory in cluster. | Check `values-local.yaml` resources (keep them small).      |

---

## 9. Master Configuration Reference (The Real Code)

If you are setting this up from scratch, copy-paste these exact contents into your files. This is the **actual logic**
that makes the "Step-by-Step" work.

### A. `k8s/chatapp/Chart.yaml`

This file tells Helm what external databases (Postgres, etc.) to download.

```yaml
apiVersion: v2
name: chatapp
description: A Helm chart for Messenger Clone
type: application
version: 0.1.0
appVersion: "1.0.0"

dependencies:
  - name: postgresql
    version: 15.5.21
    repository: https://charts.bitnami.com/bitnami
  - name: redis
    version: 19.6.1
    repository: https://charts.bitnami.com/bitnami
  - name: kafka
    version: 29.3.5
    repository: https://charts.bitnami.com/bitnami
```

### B. `k8s/chatapp/values.yaml` (Base Config)

This is your "Settings Menu". Every value here can be used in your templates.

```yaml
replicaCount: 1
image:
  repository: teri1712/chat-app
  tag: "latest"

service:
  type: ClusterIP
  port: 80

# Environment variables for your Java code
env:
  DATABASE_URL: "jdbc:postgresql://messenger-postgresql:5432/chatapp"
  DATABASE_USERNAME: "root"
  REDIS_HOST: "messenger-redis-master"
  KAFKA_BOOTSTRAP_SERVERS: "messenger-kafka:9092"
  AWS_S3_REGION: "ap-southeast-1"
  AWS_S3_BUCKET: "decade-bucket"

# Database/Cache Settings (Configuring the Bitnami Charts)
postgresql:
  auth: { database: chatapp, username: root, password: root }
  primary: { persistence: { enabled: false } }

redis:
  auth: { enabled: false }
  master: { persistence: { enabled: false } }

kafka:
  persistence: { enabled: false }
  zookeeper: { persistence: { enabled: false } }
```

### C. `k8s/chatapp/templates/deployment.yaml`

This is the "Blueprint" for your app's container. It uses `{{ ... }}` to pull values from `values.yaml`.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: { { include "chatapp.fullname" . } }
spec:
  replicas: { { .Values.replicaCount } }
  selector:
    matchLabels:
      { { - include "chatapp.selectorLabels" . | nindent 6 } }
  template:
    metadata:
      labels:
        { { - include "chatapp.selectorLabels" . | nindent 8 } }
    spec:
      containers:
        - name: { { .Chart.Name } }
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          ports:
            - name: http
              containerPort: 8080
          env:
            - name: SPRING_DATASOURCE_URL
              value: { { .Values.env.DATABASE_URL | quote } }
            - name: SPRING_DATASOURCE_USERNAME
              value: { { .Values.env.DATABASE_USERNAME | quote } }
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef: { name: chatapp-secrets, key: postgres-password }
            - name: SPRING_DATA_REDIS_HOST
              value: { { .Values.env.REDIS_HOST | quote } }
            - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
              value: { { .Values.env.KAFKA_BOOTSTRAP_SERVERS | quote } }
            - name: JWT_SECRET
              valueFrom:
                secretKeyRef: { name: chatapp-secrets, key: jwt-secret }
            - name: AWS_S3_REGION
              value: { { .Values.env.AWS_S3_REGION | quote } }
            - name: AWS_S3_BUCKET
              value: { { .Values.env.AWS_S3_BUCKET | quote } }
            - name: AWS_ACCESS_KEY_ID
              valueFrom:
                secretKeyRef: { name: chatapp-secrets, key: aws-access-id }
            - name: AWS_SECRET_ACCESS_KEY
              valueFrom:
                secretKeyRef: { name: chatapp-secrets, key: aws-secret-key }
```

### D. `k8s/chatapp/values-local.yaml` (Local Overrides)

Use this for `kind` to save your laptop's memory.

```yaml
replicaCount: 1
postgresql:
  primary:
    persistence: { enabled: false }
    resources: { requests: { cpu: "100m", memory: "256Mi" } }
redis:
  master:
    persistence: { enabled: false }
kafka:
  persistence: { enabled: false }
```

### E. `k8s/chatapp/values-prod.yaml` (Production Overrides)

Use this for GKE to enable security and auto-scaling.

```yaml
replicaCount: 3
postgresql:
  auth:
    existingSecret: "chatapp-secrets"
    secretKeys: { adminPasswordKey: "postgres-password", userPasswordKey: "postgres-password" }
  primary: { persistence: { enabled: true, size: 10Gi } }
redis:
  auth: { enabled: true, existingSecret: "chatapp-secrets", existingSecretPasswordKey: "redis-password" }
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
```

### F. `k8s/chatapp/templates/service.yaml`

This makes your app reachable via a permanent name.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: { { include "chatapp.fullname" . } }
spec:
  type: { { .Values.service.type } }
  ports:
    - port: { { .Values.service.port } }
      targetPort: 8080
      name: http
  selector:
    { { - include "chatapp.selectorLabels" . | nindent 4 } }
```

### G. `k8s/chatapp/templates/servicemonitor.yaml`

This allows Prometheus to automatically find your app.

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: { { include "chatapp.fullname" . } }
  labels:
    release: obs
spec:
  selector:
    matchLabels:
      { { - include "chatapp.selectorLabels" . | nindent 6 } }
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
```

---
