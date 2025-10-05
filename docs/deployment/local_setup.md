# Photo CRM SaaS - Local Development Setup

This guide will help you set up the Photo CRM SaaS platform for local development.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Docker** (20.10+) and **Docker Compose** (2.0+)
- **Java 17** (for backend development)
- **Node.js 18+** (for frontend development)
- **Maven 3.8+** (for backend builds)
- **Git** (for version control)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd photo-crm-saas
```

### 2. Run the Setup Script

```bash
chmod +x scripts/setup-dev.sh
./scripts/setup-dev.sh
```

This script will:
- Start all required services (PostgreSQL, Redis, MinIO)
- Run database migrations
- Build and start the application
- Verify all services are running

### 3. Access the Application

Once the setup is complete, you can access:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin123)

## Manual Setup

If you prefer to set up the environment manually:

### 1. Start Infrastructure Services

```bash
docker-compose up -d postgres redis minio
```

### 2. Wait for Services to be Ready

```bash
# Check PostgreSQL
docker-compose exec postgres pg_isready -U photocrm_user -d photocrm_dev

# Check Redis
docker-compose exec redis redis-cli ping

# Check MinIO
curl http://localhost:9000/minio/health/live
```

### 3. Run Database Migrations

```bash
cd backend
./mvnw flyway:migrate
cd ..
```

### 4. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 5. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

## Development Workflow

### Backend Development

1. **Make changes** to Java source files in `backend/src/main/java/`
2. **Run tests** with `./mvnw test`
3. **Restart the application** - Spring Boot DevTools will auto-reload
4. **Check logs** with `docker-compose logs -f backend`

### Frontend Development

1. **Make changes** to React components in `frontend/src/`
2. **Hot reload** is enabled - changes appear automatically
3. **Run tests** with `npm test`
4. **Build for production** with `npm run build`

### Database Changes

1. **Create migration** in `backend/src/main/resources/db/migration/`
2. **Run migration** with `./mvnw flyway:migrate`
3. **Verify changes** in the database

## Environment Configuration

### Backend Configuration

The backend uses Spring profiles for configuration:

- **Development**: `application-dev.yml`
- **Production**: `application-prod.yml`

Key configuration options:

```yaml
# Database
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/photocrm_dev
    username: photocrm_user
    password: photocrm_password

# Redis
spring:
  data:
    redis:
      host: localhost
      port: 6379

# S3 (MinIO)
aws:
  s3:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin123
```

### Frontend Configuration

The frontend uses environment variables:

```bash
# API Configuration
VITE_API_URL=http://localhost:8080/api
VITE_S3_URL=http://localhost:9000
```

## Testing

### Backend Tests

```bash
cd backend
./mvnw test                    # Run all tests
./mvnw test -Dtest=UserService # Run specific test class
./mvnw test -Dtest=*Test       # Run all test classes
```

### Frontend Tests

```bash
cd frontend
npm test                       # Run all tests
npm test -- --watch           # Run tests in watch mode
npm test -- --coverage        # Run tests with coverage
```

### Integration Tests

```bash
cd backend
./mvnw verify                  # Run integration tests
```

## Database Management

### Accessing the Database

```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U photocrm_user -d photocrm_dev

# Or use a GUI tool
# Host: localhost
# Port: 5432
# Database: photocrm_dev
# Username: photocrm_user
# Password: photocrm_password
```

### Running Migrations

```bash
cd backend
./mvnw flyway:migrate          # Run pending migrations
./mvnw flyway:info             # Show migration status
./mvnw flyway:validate         # Validate migrations
```

### Resetting the Database

```bash
# Stop the application
docker-compose down

# Remove volumes (this will delete all data)
docker-compose down -v

# Start fresh
docker-compose up -d postgres redis minio
cd backend && ./mvnw flyway:migrate && cd ..
```

## File Storage (MinIO)

### Accessing MinIO Console

1. Open http://localhost:9001
2. Login with `minioadmin` / `minioadmin123`
3. Create a bucket named `photocrm-photos`

### Managing Files

```bash
# List files
docker-compose exec minio mc ls minio/photocrm-photos/

# Copy files
docker-compose exec minio mc cp local-file.jpg minio/photocrm-photos/

# Remove files
docker-compose exec minio mc rm minio/photocrm-photos/file.jpg
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

```bash
# Check what's using the port
lsof -i :8080
lsof -i :3000

# Kill the process
kill -9 <PID>
```

#### 2. Database Connection Issues

```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres
```

#### 3. Frontend Build Issues

```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear Vite cache
rm -rf .vite
```

#### 4. Backend Build Issues

```bash
# Clean Maven cache
./mvnw clean

# Clear Maven local repository
rm -rf ~/.m2/repository
```

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Performance Issues

```bash
# Check resource usage
docker stats

# Check disk usage
docker system df

# Clean up unused resources
docker system prune
```

## Development Tips

### 1. Hot Reloading

- **Backend**: Spring Boot DevTools enables hot reloading
- **Frontend**: Vite provides instant hot module replacement

### 2. Debugging

- **Backend**: Use your IDE's debugger with remote debugging
- **Frontend**: Use browser dev tools and React DevTools

### 3. Code Quality

```bash
# Backend linting
./mvnw spotbugs:check
./mvnw checkstyle:check

# Frontend linting
npm run lint
npm run lint:fix
```

### 4. Database Schema Changes

1. Create a new migration file in `backend/src/main/resources/db/migration/`
2. Use Flyway naming convention: `V{version}__{description}.sql`
3. Test the migration locally before committing

### 5. API Testing

- Use the Swagger UI at http://localhost:8080/swagger-ui.html
- Import the OpenAPI spec into Postman or Insomnia
- Use curl for quick API tests

## Next Steps

1. **Read the API documentation** at http://localhost:8080/swagger-ui.html
2. **Explore the codebase** starting with the main application classes
3. **Check the architecture documentation** in `docs/architecture/`
4. **Join the development team** and start contributing!

## Support

If you encounter any issues:

1. Check the troubleshooting section above
2. Search existing GitHub issues
3. Create a new issue with detailed information
4. Contact the development team