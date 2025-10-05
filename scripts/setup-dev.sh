#!/bin/bash

# Photo CRM SaaS - Development Setup Script
# This script sets up the development environment

set -e

echo "🚀 Setting up Photo CRM SaaS development environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file..."
    cat > .env << EOF
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/photocrm_dev
DATABASE_USERNAME=photocrm_user
DATABASE_PASSWORD=photocrm_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# S3 Configuration (MinIO)
S3_ENDPOINT=http://localhost:9000
S3_ACCESS_KEY=minioadmin
S3_SECRET_KEY=minioadmin123
S3_BUCKET_NAME=photocrm-photos

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production

# Payment Configuration
STRIPE_PUBLIC_KEY=pk_test_your_stripe_public_key
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

RAZORPAY_KEY_ID=rzp_test_your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
EOF
    echo "✅ .env file created"
else
    echo "✅ .env file already exists"
fi

# Start infrastructure services
echo "🐳 Starting infrastructure services..."
docker-compose up -d postgres redis minio

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if PostgreSQL is ready
echo "🔍 Checking PostgreSQL connection..."
until docker-compose exec postgres pg_isready -U photocrm_user -d photocrm_dev; do
    echo "⏳ Waiting for PostgreSQL..."
    sleep 2
done
echo "✅ PostgreSQL is ready"

# Check if Redis is ready
echo "🔍 Checking Redis connection..."
until docker-compose exec redis redis-cli ping; do
    echo "⏳ Waiting for Redis..."
    sleep 2
done
echo "✅ Redis is ready"

# Check if MinIO is ready
echo "🔍 Checking MinIO connection..."
until curl -f http://localhost:9000/minio/health/live; do
    echo "⏳ Waiting for MinIO..."
    sleep 2
done
echo "✅ MinIO is ready"

# Run database migrations
echo "🗄️ Running database migrations..."
cd backend
./mvnw flyway:migrate
cd ..

# Build and start the application
echo "🏗️ Building and starting the application..."
docker-compose up -d backend frontend

# Wait for application to be ready
echo "⏳ Waiting for application to be ready..."
sleep 30

# Check if backend is ready
echo "🔍 Checking backend health..."
until curl -f http://localhost:8080/api/actuator/health; do
    echo "⏳ Waiting for backend..."
    sleep 5
done
echo "✅ Backend is ready"

# Check if frontend is ready
echo "🔍 Checking frontend..."
until curl -f http://localhost:3000; do
    echo "⏳ Waiting for frontend..."
    sleep 5
done
echo "✅ Frontend is ready"

echo ""
echo "🎉 Development environment is ready!"
echo ""
echo "📱 Application URLs:"
echo "   Frontend: http://localhost:3000"
echo "   Backend API: http://localhost:8080/api"
echo "   API Documentation: http://localhost:8080/swagger-ui.html"
echo "   MinIO Console: http://localhost:9001 (minioadmin/minioadmin123)"
echo ""
echo "🔧 Useful commands:"
echo "   View logs: docker-compose logs -f"
echo "   Stop services: docker-compose down"
echo "   Restart services: docker-compose restart"
echo "   Clean up: docker-compose down -v"
echo ""
echo "📚 Next steps:"
echo "   1. Open http://localhost:3000 in your browser"
echo "   2. Create a new tenant account"
echo "   3. Start developing!"