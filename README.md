# WebProjectSQLDrupalK8s
A web project deployed on Kubernetes using Drupal frontend and MySQL backend with Argo CD for GitOps.

1. Build the JAR:
   mvnw package -DskipTests
2. Build Docker image:
   docker build -t webform:local -f webform/Dockerfile webform
3. Run locally:
   docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://<db-host>:3306/mydb -e SPRING_DATASOURCE_USERNAME=user -e SPRING_DATASOURCE_PASSWORD=pass webform:local

(See build files: [`webform/pom.xml`](webform/pom.xml), [`webform/Dockerfile`](webform/Dockerfile))

## Deploy to Kubernetes (basic)
1. Create secrets:
   kubectl apply -f Deployment/secret.yaml
2. Create PV/PVC (if using local storage):
   kubectl apply -f Deployment/pv.yaml
   kubectl apply -f Deployment/pvc.yaml
3. Deploy MySQL and app:
   kubectl apply -f Deployment/backend.yaml
   kubectl apply -f Deployment/frontend.yaml
4. Deploy Drupal (optional):
   kubectl apply -f Deployment/drupal-mysql.yaml
   kubectl apply -f Deployment/drupal.yaml
5. Configure ingress / ALB as needed:
   kubectl apply -f Deployment/Albingress.yaml
