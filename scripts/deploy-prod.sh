#!/bin/bash

# Photo CRM SaaS - Production Deployment Script
# This script deploys the application to production

set -e

echo "🚀 Deploying Photo CRM SaaS to production..."

# Check if required tools are installed
command -v kubectl >/dev/null 2>&1 || { echo "❌ kubectl is required but not installed. Aborting." >&2; exit 1; }
command -v helm >/dev/null 2>&1 || { echo "❌ helm is required but not installed. Aborting." >&2; exit 1; }

# Set variables
NAMESPACE="photocrm"
RELEASE_NAME="photocrm"
IMAGE_TAG=${1:-"latest"}

echo "📦 Building Docker images..."

# Build backend image
echo "🏗️ Building backend image..."
cd backend
docker build -t photocrm/backend:${IMAGE_TAG} .
cd ..

# Build frontend image
echo "🏗️ Building frontend image..."
cd frontend
docker build -t photocrm/frontend:${IMAGE_TAG} .
cd ..

echo "✅ Docker images built successfully"

# Check if namespace exists
if ! kubectl get namespace ${NAMESPACE} >/dev/null 2>&1; then
    echo "📝 Creating namespace..."
    kubectl create namespace ${NAMESPACE}
fi

# Apply Kubernetes manifests
echo "📋 Applying Kubernetes manifests..."

# Apply namespace
kubectl apply -f infrastructure/k8s/namespace.yml

# Apply configmap
kubectl apply -f infrastructure/k8s/configmap.yml

# Apply secrets (make sure to update with real values)
echo "⚠️  Please update the secrets in infrastructure/k8s/secrets.yml with real values before proceeding"
read -p "Press Enter to continue after updating secrets..."

kubectl apply -f infrastructure/k8s/secrets.yml

# Apply services
kubectl apply -f infrastructure/k8s/service.yml

# Apply deployments
kubectl apply -f infrastructure/k8s/deployment.yml

# Apply ingress
kubectl apply -f infrastructure/k8s/ingress.yml

echo "✅ Kubernetes manifests applied successfully"

# Wait for deployments to be ready
echo "⏳ Waiting for deployments to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/photocrm-backend -n ${NAMESPACE}
kubectl wait --for=condition=available --timeout=300s deployment/photocrm-frontend -n ${NAMESPACE}

echo "✅ Deployments are ready"

# Check pod status
echo "🔍 Checking pod status..."
kubectl get pods -n ${NAMESPACE}

# Check service status
echo "🔍 Checking service status..."
kubectl get services -n ${NAMESPACE}

# Check ingress status
echo "🔍 Checking ingress status..."
kubectl get ingress -n ${NAMESPACE}

echo ""
echo "🎉 Production deployment completed successfully!"
echo ""
echo "📱 Application URLs:"
echo "   Frontend: https://app.photocrm.com"
echo "   Backend API: https://api.photocrm.com"
echo "   API Documentation: https://api.photocrm.com/swagger-ui.html"
echo ""
echo "🔧 Useful commands:"
echo "   View logs: kubectl logs -f deployment/photocrm-backend -n ${NAMESPACE}"
echo "   Scale backend: kubectl scale deployment photocrm-backend --replicas=5 -n ${NAMESPACE}"
echo "   Restart deployment: kubectl rollout restart deployment/photocrm-backend -n ${NAMESPACE}"
echo "   Check status: kubectl get all -n ${NAMESPACE}"
echo ""
echo "📚 Next steps:"
echo "   1. Configure your DNS to point to the load balancer"
echo "   2. Set up SSL certificates"
echo "   3. Configure monitoring and logging"
echo "   4. Set up backup procedures"