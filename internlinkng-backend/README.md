# InternLinkNG Backend

A Ktor-based backend server for the InternLinkNG hospital internship application platform.

## Tech Stack

- **Framework**: Ktor 2.3.7
- **Database**: PostgreSQL 15+
- **ORM**: Exposed (JetBrains)
- **Authentication**: JWT
- **Language**: Kotlin
- **Build Tool**: Gradle

## Prerequisites

1. **PostgreSQL Database**
   - Install PostgreSQL 15 or higher
   - Create a database named `internlinkng`
   - Default credentials: `postgres` / `Android_Studio1`

2. **Java 17**
   - Ensure Java 17 is installed and set as JAVA_HOME

## Database Setup

### Option 1: Using Docker (Recommended)

```bash
# Run PostgreSQL in Docker
docker run --name internlinkng-postgres \
  -e POSTGRES_DB=internlinkng \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=Android_Studio1 \
  -p 5432:5432 \
  -d postgres:15
```

### Option 2: Local PostgreSQL Installation

1. Install PostgreSQL on your system
2. Create the database:
```sql
CREATE DATABASE internlinkng;
CREATE USER postgres WITH PASSWORD 'Android_Studio1';
GRANT ALL PRIVILEGES ON DATABASE internlinkng TO postgres;
```

## Running the Backend

### Development Mode

```bash
# Navigate to backend directory
cd internlinkng-backend

# Run the server
./gradlew run
```

The server will start on `http://localhost:8080`

### Production Build

```bash
# Build the JAR
./gradlew build

# Run the JAR
java -jar build/libs/internlinkng-backend-1.0.0.jar
```

## API Endpoints

### Authentication
- `POST /login` - User login
- `POST /signup` - User registration
- `POST /apply` - Submit application

### Hospitals
- `GET /hospitals` - Get all hospitals
- `POST /hospitals` - Create new hospital (Admin only)
- `PUT /hospitals/{id}` - Update hospital (Admin only)
- `DELETE /hospitals/{id}` - Delete hospital (Admin only)

### Admin Endpoints
- `GET /admin/hospitals` - Get all hospitals (Admin)
- `POST /admin/hospitals` - Create hospital (Admin)
- `PUT /admin/hospitals/{id}` - Update hospital (Admin)
- `DELETE /admin/hospitals/{id}` - Delete hospital (Admin)

## Database Schema

### Users Table
- `id` (UUID) - Primary key
- `email` (VARCHAR) - User email
- `password_hash` (VARCHAR) - BCrypt hashed password
- `is_admin` (BOOLEAN) - Admin flag

### Hospitals Table
- `id` (UUID) - Primary key
- `name` (VARCHAR) - Hospital name
- `state` (VARCHAR) - State location
- `professions` (VARCHAR) - Comma-separated professions
- `salary_range` (VARCHAR) - Salary range
- `deadline` (VARCHAR) - Application deadline
- `online_application` (BOOLEAN) - Online application available
- `application_url` (VARCHAR) - Application URL (nullable)
- `physical_address` (VARCHAR) - Physical address

### Applications Table
- `id` (UUID) - Primary key
- `user_id` (UUID) - User reference
- `hospital_id` (UUID) - Hospital reference
- `profession` (VARCHAR) - Applied profession
- `cover_letter` (TEXT) - Cover letter (nullable)
- `status` (VARCHAR) - Application status
- `created_at` (TIMESTAMP) - Creation timestamp

## Configuration

The database configuration is in `src/main/resources/application.conf`:

```hocon
database {
    url = "jdbc:postgresql://localhost:5432/internlinkng"
    driver = org.postgresql.Driver
    user = postgres
    password = Android_Studio1
}
```

## Development

### Adding New Endpoints

1. Create route functions in `src/main/kotlin/com/internlinkng/backend/routes/`
2. Register routes in `Application.kt`
3. Add corresponding data models if needed

### Database Migrations

The database schema is automatically created on startup. For production, consider using proper migration tools.

## Testing

### Test Database Connectivity

```bash
curl http://localhost:8080/hospitals/test-db
```

### Test Authentication

```bash
# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"pascalezeke@gmail.com","password":"Android_Studio1"}'
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure PostgreSQL is running
   - Check database credentials in `application.conf`
   - Verify database exists

2. **Port Already in Use**
   - Change port in `application.conf`
   - Kill existing process on port 8080

3. **JWT Token Issues**
   - Check JWT secret in `JwtConfig.kt`
   - Verify token expiration settings

## Security Notes

- Change default passwords in production
- Use environment variables for sensitive data
- Implement proper input validation
- Add rate limiting for production
- Use HTTPS in production

## Next Steps

1. **Enhanced Authentication**
   - Password reset functionality
   - Email verification
   - Role-based access control

2. **Application Management**
   - Application status tracking
   - Email notifications
   - Document uploads

3. **Admin Dashboard**
   - Hospital management interface
   - Application review system
   - Analytics and reporting

4. **API Documentation**
   - OpenAPI/Swagger documentation
   - Postman collection
   - API versioning 