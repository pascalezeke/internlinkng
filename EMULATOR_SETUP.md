# Android Emulator Setup Guide

## ✅ Emulator Configuration Updated

The Android app has been updated to work with Android emulators. The key changes made:

### 🔧 Configuration Changes

1. **Base URL Updated**: Changed from `http://10.230.79.90:8080/` to `http://10.0.2.2:8080/`
2. **Emulator IP Address**: `10.0.2.2` is the special IP that Android emulators use to connect to the host machine
3. **Fallback Support**: Still includes physical device IPs as fallback options

### 📱 Testing on Android Emulator

#### Prerequisites
1. **Android Studio**: Make sure you have Android Studio installed
2. **Android Emulator**: Create and start an Android emulator
3. **Docker Backend**: Ensure the backend is running with `docker-compose up -d`
4. **Host Machine**: The emulator will connect to your computer's localhost

#### Step-by-Step Testing

1. **Start the Backend**
   ```bash
   # Navigate to your project directory
   cd /c/Users/pasca/Interns
   
   # Start the Docker backend
   docker-compose up -d
   
   # Verify backend is running
   docker-compose ps
   ```

2. **Test Backend from Host**
   ```bash
   # Test the API from your computer
   curl http://localhost:8080/hospitals
   ```

3. **Start Android Emulator**
   - Open Android Studio
   - Go to **Tools > AVD Manager**
   - Create a new virtual device or start an existing one
   - Make sure the emulator is running

4. **Build and Install the App**
   ```bash
   # Build the APK
   ./gradlew assembleDebug
   
   # Install on emulator
   ./gradlew installDebug
   ```

5. **Test the App**
   - Open the app on the emulator
   - Check the logs in Android Studio for connection status
   - Try logging in with: `pascalezeke@gmail.com` / `Android_Studio1`

### 🔍 Debugging Emulator Issues

#### Check Logs in Android Studio
1. Open Android Studio
2. Go to **Logcat**
3. Filter by tag: `MainActivity`, `MainViewModel`, `NetworkUtils`
4. Look for connection test results

#### Common Issues and Solutions

**Issue: "Network error" or "Connection failed"**
- **Solution**: Ensure Docker backend is running: `docker-compose ps`
- **Solution**: Test from host: `curl http://localhost:8080/hospitals`
- **Solution**: Check emulator is running and connected

**Issue: "Timeout" errors**
- **Solution**: Increase timeout in MainActivity.kt (already set to 30 seconds)
- **Solution**: Check if emulator has internet access

**Issue: "SSL/TLS" errors**
- **Solution**: App is configured for HTTP (not HTTPS) - this is expected for development

### 🛠️ Development Commands

```bash
# Build the app
./gradlew build

# Install on emulator
./gradlew installDebug

# Run tests
./gradlew test

# Clean and rebuild
./gradlew clean build

# View connected devices
adb devices
```

### 📊 Expected Behavior

**On App Launch:**
1. Network test runs automatically
2. Logs show connection status
3. If successful: "Backend connection test: SUCCESS"
4. If failed: "Backend connection test: FAILED"

**Login Test:**
1. Use credentials: `pascalezeke@gmail.com` / `Android_Studio1`
2. Should receive JWT token
3. Should see hospital list

**Hospital List:**
1. Should display 3 sample hospitals
2. Lagos General Hospital
3. Abuja Teaching Hospital
4. Ibadan University Teaching Hospital

### 🔧 Network Configuration

**Current Settings:**
- **Base URL**: `http://10.0.2.2:8080/` (emulator to host)
- **Timeout**: 30 seconds
- **Protocol**: HTTP (for development)
- **CORS**: Enabled on backend

**IP Address Mapping:**
- `10.0.2.2` → Host machine's localhost (for Android emulator)
- `10.0.3.2` → Host machine's localhost (for Genymotion emulator)
- `10.230.79.90` → Physical device IP (fallback)

### 📱 App Features Ready

✅ **Authentication**
- Login/Signup
- JWT token management
- Session persistence

✅ **Hospital Management**
- List all hospitals
- Search and filter
- Hospital details
- Application submission

✅ **Admin Features**
- Add/Edit/Delete hospitals
- View applications
- Manage users

✅ **Offline Support**
- Local database caching
- Offline hospital viewing
- Sync when online

### 🚀 Quick Start

1. **Start Backend**
   ```bash
   docker-compose up -d
   ```

2. **Start Emulator**
   - Open Android Studio
   - Start an Android emulator

3. **Build and Install**
   ```bash
   ./gradlew installDebug
   ```

4. **Test the App**
   - Open the app
   - Check logs for connection status
   - Try logging in

---

🎉 **Status: READY FOR EMULATOR TESTING** - Your Android app is now configured to work with Android emulators! 