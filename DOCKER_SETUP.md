# InternLinkNG Docker Setup

## ✅ Successfully Running in Docker

The entire InternLinkNG application is now running in Docker containers:

### 🐳 Running Containers

1. **PostgreSQL Database** (`internlinkng-postgres`)
   - Port: `5432`
   - Database: `internlinkng`
   - Status: ✅ Healthy

2. **Ktor Backend** (`internlinkng-backend`)
   - Port: `8080`
   - API: `http://localhost:8080`
   - Status: ✅ Running

### 🚀 Quick Start Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up --build
```

### 🔗 API Endpoints (Working)

- **GET** `http://localhost:8080/hospitals` - List all hospitals
- **POST** `http://localhost:8080/login` - User authentication
- **POST** `http://localhost:8080/signup` - User registration
- **POST** `http://localhost:8080/apply` - Submit application

### 🗄️ Database Status

- ✅ PostgreSQL 15 running
- ✅ Database `internlinkng` created
- ✅ Tables created automatically
- ✅ Sample data inserted
- ✅ Admin user created: `pascalezeke@gmail.com`

### 🔐 Authentication

**Admin Credentials:**
- Email: `pascalezeke@gmail.com`
- Password: `Android_Studio1`

**Test Login:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"pascalezeke@gmail.com","password":"Android_Studio1"}'
```

### 📊 Sample Data

The backend automatically inserts sample hospitals:
1. **Lagos General Hospital** - Lagos
2. **Abuja Teaching Hospital** - Abuja  
3. **Ibadan University Teaching Hospital** - Oyo

### 🛠️ Development

**View Backend Logs:**
```bash
docker-compose logs backend
```

**View Database Logs:**
```bash
docker-compose logs postgres
```

**Access Database:**
```bash
docker exec -it internlinkng-postgres psql -U postgres -d internlinkng
```

### 🔧 Configuration

- **Database URL**: `jdbc:postgresql://postgres:5432/internlinkng`
- **Database User**: `postgres`
- **Database Password**: `Android_Studio1`
- **Backend Port**: `8080`
- **Database Port**: `5432`

### 📱 Next Steps

1. **Android App Integration**
   - Update Android app to use `http://localhost:8080` as API base URL
   - Test authentication flow
   - Implement hospital listing

2. **Enhanced Features**
   - Add more API endpoints
   - Implement file uploads
   - Add email notifications
   - Create admin dashboard

3. **Production Deployment**
   - Use environment variables for secrets
   - Add SSL/TLS certificates
   - Implement proper logging
   - Add monitoring and health checks

### 🐛 Troubleshooting

**If containers won't start:**
```bash
# Clean up
docker-compose down -v
docker system prune -f

# Rebuild
docker-compose up --build
```

**If database connection fails:**
```bash
# Check database logs
docker-compose logs postgres

# Restart database
docker-compose restart postgres
```

**If backend won't start:**
```bash
# Check backend logs
docker-compose logs backend

# Rebuild backend
docker-compose up --build backend
```

---

🎉 **Status: FULLY OPERATIONAL** - Your InternLinkNG backend is running successfully in Docker! 