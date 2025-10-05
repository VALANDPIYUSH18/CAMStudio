# Photo CRM SaaS

A comprehensive multi-tenant SaaS platform for photography studios to manage orders, photos, clients, and payments.

## Architecture Overview

This project implements a cloud-native, multi-tenant SaaS platform using modern microservices architecture with the following key features:

- **Multi-tenant Data Isolation**: Schema-based multi-tenancy in PostgreSQL with row-level security
- **High-Volume Photo Management**: Efficient handling of large RAW image files with S3 storage and CDN
- **Real-time Collaboration**: WebSocket connections for real-time updates and optimistic locking
- **Payment Processing Security**: PCI DSS compliant payment processing with webhook handling
- **Performance at Scale**: Support for 1000+ concurrent users with <200ms API response times

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.x with Java 17
- **Database**: PostgreSQL 15+ with multi-tenant schema design
- **Caching**: Redis for session management and caching
- **Storage**: AWS S3 for photo storage with CloudFront CDN
- **Authentication**: JWT tokens with refresh token rotation

### Frontend
- **Framework**: React 18 with TypeScript
- **Styling**: Tailwind CSS with Shadcn-ui components
- **State Management**: Zustand with TanStack Query
- **Build Tool**: Vite for fast development and building

### Infrastructure
- **Containerization**: Docker with Kubernetes orchestration
- **Cloud**: AWS with Terraform for infrastructure as code
- **CI/CD**: GitHub Actions with automated testing and deployment
- **Monitoring**: Prometheus, Grafana, and ELK stack

## Project Structure

```
photo-crm-saas/
├── backend/                 # Spring Boot backend application
├── frontend/               # React frontend application
├── infrastructure/         # Terraform and Kubernetes configurations
├── docs/                  # Documentation and API specifications
├── scripts/               # Deployment and utility scripts
└── docker-compose.yml     # Local development environment
```

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker and Docker Compose
- PostgreSQL 15+
- Redis 7+

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd photo-crm-saas
   ```

2. **Start the development environment**
   ```bash
   docker-compose up -d
   ```

3. **Run the backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

4. **Run the frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

## Features

### Studio Admin Features
- Dashboard with studio metrics and recent orders
- Order management and photographer assignment
- Team management with role-based access control
- Financial operations with invoice generation and payment tracking

### Staff Photographer Features
- Order assignment notifications
- Batch photo upload with progress tracking
- Client communication tools
- Real-time collaboration with team members

### Client Features
- Gallery access via QR code or direct link
- Photo selection with responsive grid interface
- Secure payment processing
- Download access for purchased photos

## Security

- Multi-tenant data isolation with row-level security
- JWT-based authentication with refresh token rotation
- PCI DSS compliant payment processing
- Encryption at rest and in transit
- Comprehensive audit logging

## Performance

- Horizontal scaling with Kubernetes
- Database read replicas for query optimization
- Redis caching for improved response times
- CDN integration for global photo delivery
- Optimized image processing with Lambda functions

## Monitoring

- Application metrics with Prometheus
- Centralized logging with ELK stack
- Distributed tracing with Jaeger
- Health checks and alerting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact the development team or create an issue in the repository.