# Real Backend Setup with PostgreSQL

This guide will help you set up the real backend server with PostgreSQL database for your Android app.

## 🎯 What You'll Get

✅ **Real Database**: PostgreSQL with persistent data storage
✅ **Admin Panel**: Admins can post real hospital data
✅ **User Management**: Real user registration and authentication
✅ **Hospital Management**: Add, edit, delete hospitals
✅ **Application System**: Users can apply to hospitals
✅ **JWT Authentication**: Secure login system

## 🚀 Step 1: Install PostgreSQL

### Windows Installation

#### Option A: Download Installer
1. Go to https://www.postgresql.org/download/windows/
2. Download PostgreSQL 15 or higher
3. Run the installer
4. Set password as: `Android_Studio1`
5. Keep default port: `5432`

#### Option B: Using Chocolatey (if you have it)
```powershell
choco install postgresql15
```

### macOS Installation
```bash
# Using Homebrew
brew install postgresql@15
brew services start postgresql@15
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## 🚀 Step 2: Setup Database

### Windows (if using installer)
1. PostgreSQL should be running automatically
2. Open Command Prompt as Administrator
3. Navigate to PostgreSQL bin directory:
```cmd
cd "C:\Program Files\PostgreSQL\15\bin"
```

### macOS/Linux
```bash
# Connect to PostgreSQL
sudo -u postgres psql
```

### Create Database and User
```sql
-- Create the database
CREATE DATABASE internlinkng;

-- Create user (if not exists)
CREATE USER postgres WITH PASSWORD 'Android_Studio1';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE internlinkng TO postgres;

-- Connect to the database
\c internlinkng

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO postgres;

-- Exit
\q
```

## 🚀 Step 3: Install Java 17

### Windows
1. Download OpenJDK 17 from: https://adoptium.net/
2. Install and set JAVA_HOME environment variable

### macOS
```bash
brew install openjdk@17
```

### Linux
```bash
sudo apt install openjdk-17-jdk
```

### Verify Java Installation
```bash
java -version
# Should show Java 17
```

## 🚀 Step 4: Run the Backend

### Navigate to Backend Directory
```bash
cd internlinkng-backend
```

### Build and Run
```bash
# Build the project
./gradlew build

# Run the server
./gradlew run
```

### Expected Output
```
[main] INFO  ktor.application - Application started in 0.0 seconds.
[main] INFO  ktor.application - Responding at http://0.0.0.0:8080
```

## 🚀 Step 5: Test the Backend

### Test Database Connection
```bash
curl http://localhost:8080/hospitals
```

Expected response:
```json
[]
```

### Test Admin Login
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@internlinkng.com","password":"admin123"}'
```

## 🚀 Step 6: Setup Admin User

### Create Admin Account
```bash
curl -X POST http://localhost:8080/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@internlinkng.com",
    "password": "admin123",
    "isAdmin": true
  }'
```

### Login as Admin
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@internlinkng.com","password":"admin123"}'
```

Save the JWT token from the response.

## 🚀 Step 7: Add Sample Hospitals

### Add Hospital 1
```bash
curl -X POST http://localhost:8080/admin/hospitals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Lagos General Hospital",
    "state": "Lagos",
    "professions": "Medicine, Nursing, Pharmacy",
    "salary_range": "₦50,000 - ₦100,000",
    "deadline": "2024-12-31",
    "online_application": true,
    "application_url": "https://lagoshospital.com/apply",
    "physical_address": "123 Victoria Island, Lagos"
  }'
```

### Add Hospital 2
```bash
curl -X POST http://localhost:8080/admin/hospitals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Abuja Teaching Hospital",
    "state": "Abuja",
    "professions": "Medicine, Surgery, Radiology",
    "salary_range": "₦60,000 - ₦120,000",
    "deadline": "2024-11-30",
    "online_application": true,
    "application_url": "https://abujahospital.com/apply",
    "physical_address": "456 Central District, Abuja"
  }'
```

### Add Hospital 3
```bash
curl -X POST http://localhost:8080/admin/hospitals \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Ibadan University Teaching Hospital",
    "state": "Oyo",
    "professions": "Medicine, Dentistry, Physiotherapy",
    "salary_range": "₦45,000 - ₦90,000",
    "deadline": "2024-10-31",
    "online_application": false,
    "application_url": null,
    "physical_address": "789 University Road, Ibadan"
  }'
```

## 🚀 Step 8: Test Android App

### Build and Install App
```bash
# Navigate to Android project root
cd /c/Users/pasca/Interns

# Build and install on emulator
./gradlew installDebug
```

### Test Login
Use these credentials in your Android app:
- **Email**: `pascalezeke@gmail.com`
- **Password**: `Android_Studio1`

## 📊 API Endpoints

### Authentication
- `POST /login` - User login
- `POST /signup` - User registration

### Public Endpoints
- `GET /hospitals` - Get all hospitals
- `POST /apply` - Submit application

### Admin Endpoints (require JWT token)
- `GET /admin/hospitals` - Get all hospitals
- `POST /admin/hospitals` - Create hospital
- `PUT /admin/hospitals/{id}` - Update hospital
- `DELETE /admin/hospitals/{id}` - Delete hospital

## 🔧 Troubleshooting

### PostgreSQL Issues

**Database connection failed:**
```bash
# Check if PostgreSQL is running
# Windows:
services.msc
# Look for "postgresql-x64-15" service

# macOS/Linux:
sudo systemctl status postgresql
```

**Port 5432 already in use:**
```bash
# Find process using port 5432
netstat -ano | findstr :5432  # Windows
lsof -i :5432  # macOS/Linux
```

### Backend Issues

**Port 8080 already in use:**
```bash
# Find and kill process
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # macOS/Linux
kill -9 <PID>
```

**Java not found:**
```bash
# Check Java installation
java -version
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME%  # Windows
```

### Android App Issues

**Can't connect to backend:**
```bash
# Test from computer
curl http://localhost:8080/hospitals

# Test from emulator
curl http://10.0.2.2:8080/hospitals
```

## 🎯 Admin Features

Once set up, admins can:

1. **Login** with admin credentials
2. **Add Hospitals** with real data
3. **Edit Hospitals** - update information
4. **Delete Hospitals** - remove from database
5. **View Applications** - see user submissions
6. **Manage Users** - user administration

## 📱 User Features

Users can:

1. **Register** new accounts
2. **Login** with credentials
3. **Browse Hospitals** - view all available
4. **Search Hospitals** - filter by location/profession
5. **Apply to Hospitals** - submit applications
6. **Track Applications** - view status

## 🚀 Development Commands

```bash
# Start backend
cd internlinkng-backend
./gradlew run

# Build Android app
cd /c/Users/pasca/Interns
./gradlew build

# Install on emulator
./gradlew installDebug

# View logs
./gradlew run --debug
```

## 🔒 Security Notes

- Change default passwords in production
- Use environment variables for sensitive data
- Implement proper input validation
- Add rate limiting for production
- Use HTTPS in production

---

🎉 **Your real backend is now ready!** Admins can post real hospital data that will appear in your Android app. 