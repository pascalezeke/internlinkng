# Backend Options - No Docker Required

You have several options to run the backend without Docker. Here are all the alternatives:

## 🚀 Option 1: Run Backend Directly (Recommended)

### Prerequisites
1. **Java 17** - Install Java 17 on your system
2. **PostgreSQL** - Install PostgreSQL 15+ locally

### Step 1: Install PostgreSQL

#### Windows:
```bash
# Download from https://www.postgresql.org/download/windows/
# Or use Chocolatey:
choco install postgresql15
```

#### macOS:
```bash
# Using Homebrew
brew install postgresql@15
brew services start postgresql@15
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### Step 2: Setup Database
```bash
# Connect to PostgreSQL
sudo -u postgres psql

# Create database and user
CREATE DATABASE internlinkng;
CREATE USER postgres WITH PASSWORD 'Android_Studio1';
GRANT ALL PRIVILEGES ON DATABASE internlinkng TO postgres;
\q
```

### Step 3: Run Backend
```bash
# Navigate to backend directory
cd internlinkng-backend

# Run the server
./gradlew run
```

The server will start on `http://localhost:8080`

## 🚀 Option 2: Use H2 Database (Simplest)

If you don't want to install PostgreSQL, you can modify the backend to use H2 (in-memory database).

### Step 1: Modify Backend Configuration
Edit `internlinkng-backend/src/main/resources/application.conf`:

```hocon
database {
    url = "jdbc:h2:mem:internlinkng;DB_CLOSE_DELAY=-1"
    driver = org.h2.Driver
    user = sa
    password = ""
}
```

### Step 2: Add H2 Dependency
Edit `internlinkng-backend/build.gradle.kts` and add:
```kotlin
dependencies {
    implementation("com.h2database:h2:2.2.224")
    // ... other dependencies
}
```

### Step 3: Run Backend
```bash
cd internlinkng-backend
./gradlew run
```

## 🚀 Option 3: Use SQLite (Lightweight)

### Step 1: Modify Backend Configuration
Edit `internlinkng-backend/src/main/resources/application.conf`:

```hocon
database {
    url = "jdbc:sqlite:internlinkng.db"
    driver = org.sqlite.JDBC
    user = ""
    password = ""
}
```

### Step 2: Add SQLite Dependency
Edit `internlinkng-backend/build.gradle.kts` and add:
```kotlin
dependencies {
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    // ... other dependencies
}
```

### Step 3: Run Backend
```bash
cd internlinkng-backend
./gradlew run
```

## 🚀 Option 4: Use Mock Server (Development Only)

For quick testing, you can create a simple mock server:

### Create Mock Server
Create a new file `mock-server.py`:

```python
from flask import Flask, jsonify
from flask_cors import CORS
import jwt
import datetime

app = Flask(__name__)
CORS(app)

# Mock JWT secret
JWT_SECRET = "your-secret-key"

# Mock data
hospitals = [
    {
        "id": "1",
        "name": "Lagos General Hospital",
        "state": "Lagos",
        "professions": "Medicine, Nursing, Pharmacy",
        "salary_range": "₦50,000 - ₦100,000",
        "deadline": "2024-12-31",
        "online_application": True,
        "application_url": "https://lagoshospital.com/apply",
        "physical_address": "123 Victoria Island, Lagos"
    },
    {
        "id": "2", 
        "name": "Abuja Teaching Hospital",
        "state": "Abuja",
        "professions": "Medicine, Surgery, Radiology",
        "salary_range": "₦60,000 - ₦120,000",
        "deadline": "2024-11-30",
        "online_application": True,
        "application_url": "https://abujahospital.com/apply",
        "physical_address": "456 Central District, Abuja"
    },
    {
        "id": "3",
        "name": "Ibadan University Teaching Hospital", 
        "state": "Oyo",
        "professions": "Medicine, Dentistry, Physiotherapy",
        "salary_range": "₦45,000 - ₦90,000",
        "deadline": "2024-10-31",
        "online_application": False,
        "application_url": None,
        "physical_address": "789 University Road, Ibadan"
    }
]

@app.route('/hospitals', methods=['GET'])
def get_hospitals():
    return jsonify(hospitals)

@app.route('/login', methods=['POST'])
def login():
    # Mock login - always return success
    token = jwt.encode({
        'email': 'pascalezeke@gmail.com',
        'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=24)
    }, JWT_SECRET, algorithm='HS256')
    
    return jsonify({
        'token': token,
        'user': {
            'email': 'pascalezeke@gmail.com',
            'is_admin': False
        }
    })

@app.route('/signup', methods=['POST'])
def signup():
    return jsonify({'message': 'User created successfully'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)
```

### Install Python Dependencies
```bash
pip install flask flask-cors pyjwt
```

### Run Mock Server
```bash
python mock-server.py
```

## 🚀 Option 5: Use Online Backend (External)

You can deploy the backend to a cloud service and use that URL:

### Deploy to Railway/Heroku/Render
1. Push your backend code to GitHub
2. Connect to Railway/Heroku/Render
3. Deploy automatically
4. Use the provided URL in your Android app

## 📱 Android App Configuration

For all options, your Android app is already configured to work with `http://10.0.2.2:8080/` (emulator) or `http://localhost:8080/` (physical device).

### Test the Connection
```bash
# Test from your computer
curl http://localhost:8080/hospitals

# Test from emulator (should work with 10.0.2.2)
curl http://10.0.2.2:8080/hospitals
```

## 🎯 Recommended Approach

**For Development:**
1. **Option 1** (PostgreSQL) - Most realistic, good for learning
2. **Option 2** (H2) - Simplest setup, no database installation needed
3. **Option 4** (Mock Server) - Quickest for testing UI

**For Production:**
- Use Option 1 with proper PostgreSQL setup
- Or deploy to cloud (Option 5)

## 🚀 Quick Start Commands

### Option 1 (PostgreSQL):
```bash
# Start PostgreSQL (if not running)
sudo systemctl start postgresql  # Linux
brew services start postgresql@15  # macOS

# Run backend
cd internlinkng-backend
./gradlew run
```

### Option 2 (H2):
```bash
# Modify application.conf first, then:
cd internlinkng-backend
./gradlew run
```

### Option 4 (Mock Server):
```bash
# Install Python dependencies
pip install flask flask-cors pyjwt

# Run mock server
python mock-server.py
```

## 🔧 Troubleshooting

### Common Issues:

**Port 8080 already in use:**
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 <PID>
```

**Database connection failed:**
- Check if PostgreSQL is running
- Verify database credentials
- Ensure database exists

**Android app can't connect:**
- Verify backend is running on port 8080
- Check emulator is using `10.0.2.2:8080`
- Test with curl first

---

🎉 **Choose the option that works best for you!** All of these will work with your Android emulator. 