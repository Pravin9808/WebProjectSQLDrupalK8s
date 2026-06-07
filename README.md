WebProjectSQLDrupalK8s

A cloud-native web application stack built with **Spring Boot**, **MySQL**, and **Drupal**, deployed on **Kubernetes** with **Argo CD** for GitOps automation and automated deployments.

---

📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Local Development](#-local-development)
- [Building](#-building)
- [Docker Deployment](#-docker-deployment)
- [Kubernetes Deployment](#-kubernetes-deployment)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)
- [Support](#-support)


🏗️ Overview

This project provides a complete, production-ready infrastructure for deploying a web application with:

- **Backend**: Spring Boot application with MySQL database
- **Frontend**: Drupal CMS with MySQL database
- **Orchestration**: Kubernetes with persistent storage
- **GitOps**: Argo CD for continuous deployment
- **Networking**: AWS ALB Ingress Controller for load balancing
- **Security**: Kubernetes Secrets for sensitive data management


🎯 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    AWS ALB Ingress                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼──────────┐        ┌────────▼────────┐
│  Spring Boot     │        │    Drupal       │
│  Application     │        │    Frontend     │
│  (Port 8080)     │        │  (Port 80/443)  │
└───────┬──────────┘        └────────┬────────┘
        │                            │
        └────────────┬───────────────┘
                     │
            ┌────────▼────────┐
            │  MySQL Database │
            │  (Port 3306)    │
            └─────────────────┘
```


📦 Prerequisites

Required Tools

- **Java 17+** - For building Spring Boot application
- **Maven 3.6+** - For dependency management (bundled with `mvnw`)
- **Docker** - For containerization
- **kubectl** - For Kubernetes interaction (v1.20+)
- **Docker Registry** - For storing container images (Docker Hub, ECR, etc.)

Kubernetes Cluster Requirements

- Kubernetes 1.20+ cluster
- kubectl configured to access your cluster
- Persistent Volume support (local or cloud provider)
- AWS ALB Ingress Controller (for ALB Ingress setup)
- Argo CD installed (for GitOps deployments)


🚀 Quick Start

1. Clone the Repository

```bash
git clone https://github.com/Pravin9808/WebProjectSQLDrupalK8s.git
cd WebProjectSQLDrupalK8s
```

2. Build and Deploy

```bash
# Build the Spring Boot JAR
./mvnw package -DskipTests

# Build Docker image
docker build -t webform:latest -f webform/Dockerfile webform

# Push to registry (if using remote registry)
docker tag webform:latest <your-registry>/webform:latest
docker push <your-registry>/webform:latest
```

### 3. Deploy to Kubernetes

```bash
# Create Kubernetes secrets
kubectl apply -f Deployment/secret.yaml

# Create persistent storage
kubectl apply -f Deployment/pv.yaml
kubectl apply -f Deployment/pvc.yaml

# Deploy databases and applications
kubectl apply -f Deployment/backend.yaml
kubectl apply -f Deployment/frontend.yaml
kubectl apply -f Deployment/drupal-mysql.yaml
kubectl apply -f Deployment/drupal.yaml

# Configure ingress
kubectl apply -f Deployment/Albingress.yaml
```


💻 Local Development

### Prerequisites

- Java 17+
- Maven (or use bundled `./mvnw`)
- MySQL Server running locally (or Docker container)

Setup Local Database

```bash
# Create database
mysql -u root -p -e "CREATE DATABASE mydb; CREATE USER 'user'@'localhost' IDENTIFIED BY 'pass'; GRANT ALL PRIVILEGES ON mydb.* TO 'user'@'localhost'; FLUSH PRIVILEGES;"

# Or use Docker
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=mydb \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=pass \
  -p 3306:3306 \
  mysql:8.0
```

Run Spring Boot Application

```bash
# Option 1: Using Maven
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://localhost:3306/mydb \
  --spring.datasource.username=user \
  --spring.datasource.password=pass"

# Option 2: Run built JAR
java -jar webform/target/webform-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/mydb \
  --spring.datasource.username=user \
  --spring.datasource.password=pass
```

Access the application at: `http://localhost:8080`


🔨 Building

### Build Options

```bash
# Build with tests
./mvnw clean package

# Build without tests
./mvnw clean package -DskipTests

# Build with specific profile
./mvnw clean package -P production

# Build with debug output
./mvnw clean package -X
```

### Build Artifacts

- JAR: `webform/target/webform-0.0.1-SNAPSHOT.jar`
- Docker Image: `webform:latest` (after docker build)


## 🐳 Docker Deployment

### Build Docker Image

```bash
# Build locally
docker build -t webform:local -f webform/Dockerfile webform

# Build and tag for registry
docker build -t <registry>/<repository>/webform:v1.0.0 -f webform/Dockerfile webform
```

### Run Docker Container

```bash
docker run -d \
  --name webform-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/mydb \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  webform:local
```

### View Logs

```bash
docker logs -f webform-app
```


## ☸️ Kubernetes Deployment

### Deployment Steps

#### 1. Create Kubernetes Secrets

```bash
kubectl apply -f Deployment/secret.yaml
```

**Contents**: Database credentials and authentication tokens

2. Create Persistent Storage

```bash
# Create Persistent Volume
kubectl apply -f Deployment/pv.yaml

# Create Persistent Volume Claim
kubectl apply -f Deployment/pvc.yaml
```

3. Deploy Backend (MySQL + Spring Boot)

```bash
kubectl apply -f Deployment/backend.yaml
```

Includes:
- MySQL StatefulSet with persistent storage
- Spring Boot Deployment
- Service endpoints

4. Deploy Frontend (Drupal)

```bash
kubectl apply -f Deployment/frontend.yaml
```

Includes:
- Drupal Pods
- Load Balancer Service

5. Deploy Drupal with MySQL (Optional)

```bash
# Drupal MySQL backend
kubectl apply -f Deployment/drupal-mysql.yaml

# Drupal CMS
kubectl apply -f Deployment/drupal.yaml
```

6. Configure Ingress (ALB)

```bash
kubectl apply -f Deployment/Albingress.yaml
```

This creates an AWS Application Load Balancer with:
- SSL/TLS termination
- Path-based routing
- Health checks

Verify Deployment

```bash
# Check pod status
kubectl get pods -A

# Check services
kubectl get svc -A

# Check ingress
kubectl get ingress -A

# View logs
kubectl logs -f deployment/webform -n default

# Describe resources
kubectl describe deployment webform
kubectl describe svc webform
```

### Scaling

```bash
# Scale Spring Boot application
kubectl scale deployment webform --replicas=3

# Scale Drupal frontend
kubectl scale deployment drupal --replicas=2
```

### Update Deployment

```bash
# Update image
kubectl set image deployment/webform \
  webform=<registry>/webform:v1.0.1 \
  --record

# Check rollout status
kubectl rollout status deployment/webform

# Rollback if needed
kubectl rollout undo deployment/webform
```


## 📁 Project Structure

```
WebProjectSQLDrupalK8s/
├── webform/                      # Spring Boot Application
│   ├── src/                      # Java source code
│   ├── pom.xml                   # Maven configuration
│   ├── Dockerfile                # Docker image definition
│   └── webformchart/             # Helm chart (optional)
│
├── Deployment/                   # Kubernetes manifests
│   ├── secret.yaml               # Database credentials
│   ├── pv.yaml                   # Persistent Volume
│   ├── pvc.yaml                  # Persistent Volume Claim
│   ├── backend.yaml              # MySQL + Spring Boot
│   ├── frontend.yaml             # Drupal frontend
│   ├── drupal-mysql.yaml         # Drupal MySQL
│   ├── drupal.yaml               # Drupal CMS
│   ├── Albingress.yaml           # AWS ALB Ingress
│   ├── networkingpolicy.yaml     # Network policies
│   └── db-secret.yaml            # DB credentials
│
├── cluster/                      # Cluster configuration
│   ├── cluster.yaml              # Cluster setup
│   └── iam_policy.json           # IAM policies
│
└── README.md                     # This file
```


⚙️ Configuration

#Environment Variables

#Spring Boot Application

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/mydb
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=pass
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Server
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/
```

Kubernetes Secrets

Edit `Deployment/secret.yaml` to set base64-encoded values:

```bash
# Encode credentials
echo -n 'user' | base64
echo -n 'password' | base64
```

### Database Configuration

#### MySQL Initialization

The backend.yaml creates a MySQL instance with:
- Database: `mydb`
- User: `user`
- Root Password: Set in secret.yaml

#### Schema Migration

Spring Boot automatically applies schema updates via Hibernate. Configure in `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
```


🔍 Troubleshooting

### Common Issues

#### 1. Pod Fails to Start

```bash
# Check pod logs
kubectl logs <pod-name>

# Describe pod for events
kubectl describe pod <pod-name>

# Check resource availability
kubectl top nodes
kubectl describe node <node-name>
```

#### 2. Database Connection Errors

```bash
# Verify MySQL is running
kubectl get pods -l app=mysql

# Check MySQL logs
kubectl logs -f <mysql-pod-name>

# Test connectivity
kubectl exec -it <pod-name> -- \
  mysql -h mysql -u user -p -e "SELECT 1;"
```

#### 3. Image Pull Errors

```bash
# Create image pull secret
kubectl create secret docker-registry regcred \
  --docker-server=<registry> \
  --docker-username=<username> \
  --docker-password=<password> \
  --docker-email=<email>

# Add to deployments:
# imagePullSecrets:
#   - name: regcred
```

#### 4. Persistent Volume Issues

```bash
# Check PV and PVC status
kubectl get pv,pvc

# Describe PVC for events
kubectl describe pvc <pvc-name>

# Verify storage class
kubectl get storageclass
```

#### 5. Ingress Not Working

```bash
# Check ingress status
kubectl get ingress -A -o wide

# Describe ingress
kubectl describe ingress webform-ingress

# Check ALB controller logs
kubectl logs -f -n kube-system deployment/aws-load-balancer-controller
```

### Debug Commands

```bash
# Get all resources in namespace
kubectl get all -n default

# Port forward for local testing
kubectl port-forward svc/webform 8080:8080

# Execute command in pod
kubectl exec -it <pod-name> -- /bin/bash

# Check resource usage
kubectl top pods
kubectl top nodes

# View cluster info
kubectl cluster-info
kubectl get nodes -o wide
```


📝 Additional Documentation

- **pom.xml**: [Spring Boot Maven Configuration](webform/pom.xml)
- **Dockerfile**: [Container Image Definition](webform/Dockerfile)
- **Kubernetes Manifests**: [Deployment Directory](Deployment/)
- **Cluster Config**: [Cluster Setup](cluster/)

🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Check existing documentation
- Review logs using kubectl commands

---

**Last Updated**: June 2026  
**Version**: 1.0.0
