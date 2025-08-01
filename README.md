# InternLinkNG - Medical Internship Platform

A comprehensive Android application built with Kotlin and Jetpack Compose that helps fresh Nigerian graduates in medical fields find available internship openings across Nigerian hospitals.

## 🏥 Features

### For Medical Graduates
- **Hospital Discovery**: Browse and search hospitals accepting interns
- **Advanced Filtering**: Filter by profession, state, and salary range
- **Application Tracking**: Mark hospitals you've applied to
- **Digital Applications**: Submit applications online for supported hospitals
- **Physical Application Support**: UI for courier arrangement (placeholder)
- **Offline Support**: View cached hospital data without internet

### For Administrators
- **Hospital Management**: Add, edit, and delete hospital listings
- **Application Monitoring**: View submitted applications
- **User Management**: Manage user accounts and permissions

## 🛠 Tech Stack

### Frontend (Android)
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI framework
- **MVVM Architecture**: Clean separation of concerns
- **Room Database**: Local data persistence
- **Retrofit**: Network API communication
- **Navigation Compose**: Screen navigation
- **Material Design 3**: Modern UI components

### Backend (Ktor)
- **Kotlin**: Server-side language
- **Ktor**: Lightweight web framework
- **JWT Authentication**: Secure user sessions
- **Exposed ORM**: Database operations
- **CORS Support**: Cross-origin requests
- **JSON Serialization**: Data exchange format

## 📱 Screens

1. **Login/Signup Screen**: User authentication
2. **Home Screen**: Hospital listings with search and filters
3. **Hospital Details Screen**: Detailed hospital information and application
4. **Applied Hospitals Screen**: Track your applications
5. **Settings Screen**: User preferences and logout
6. **Admin Screens**: Hospital management interface

## 🏗 Architecture

### MVVM Pattern
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel      │    │  Data Layer     │
│   (Compose)     │◄──►│  (Business      │◄──►│  (Repository)   │
│                 │    │   Logic)        │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Data Flow
1. **UI** observes ViewModel's StateFlow
2. **ViewModel** processes user actions and updates state
3. **Repository** manages data from multiple sources
4. **Room** provides offline caching
5. **Retrofit** handles API communication

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.8+
- Android SDK 33+
- Java 11+

### Backend Setup
1. Navigate to `internlinkng-backend/`
2. Run the Ktor server:
   ```bash
   ./gradlew run
   ```
3. Server starts on `http://localhost:8080`

### Android App Setup
1. Open the project in Android Studio
2. Update the backend URL in `MainActivity.kt` if needed
3. Build and run the app

## 📊 Database Schema

### Hospitals Table
- `id`: Unique identifier
- `name`: Hospital name
- `state`: Nigerian state
- `professions`: Comma-separated list of medical professions
- `salaryRange`: Salary range for interns
- `deadline`: Application deadline
- `onlineApplication`: Boolean flag for online applications
- `applicationUrl`: URL for online applications
- `physicalAddress`: Hospital address
- `isApplied`: User's application status

## 🔐 Authentication

The app supports two user types:
- **Regular Users**: Can browse hospitals and submit applications
- **Administrators**: Can manage hospital listings

### Demo Credentials
- **Admin**: `pascalezeke@gmail.com` / `Android_Studio1`
- **User**: Any email/password combination for signup

## 🌟 Key Features Implementation

### Search and Filter
- Real-time search across hospital names, states, and professions
- Filter chips for profession, state, and salary range
- Combined filtering with multiple criteria

### Offline Support
- Room database caches hospital data
- App works without internet connection
- Syncs data when connection is restored

### Application Management
- Mark hospitals as applied
- Track application status
- Submit digital applications
- View applied hospitals list

### Modern UI/UX
- Material Design 3 components
- Dark/Light theme support
- Responsive design
- Smooth animations and transitions

## 🔧 Configuration

### Backend Configuration
Update `internlinkng-backend/src/main/resources/application.conf`:
```hocon
ktor {
  deployment {
    port = 8080
    port = ${?PORT}
  }
  application {
    modules = [ com.internlinkng.backend.ApplicationKt.module ]
  }
}
```

### Android Configuration
Update `app/src/main/java/com/internlinkng/MainActivity.kt`:
```kotlin
.baseUrl("http://YOUR_BACKEND_IP:8080/")
```

## 📈 Future Enhancements

- [ ] Push notifications for new hospital listings
- [ ] Real-time courier service integration
- [ ] Document upload for applications
- [ ] Interview scheduling system
- [ ] Analytics dashboard for administrators
- [ ] Multi-language support
- [ ] Advanced search with geolocation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Developer**: [Your Name]
- **Backend**: Ktor + Kotlin
- **Frontend**: Jetpack Compose + Kotlin

---

**InternLinkNG** - Connecting medical graduates with internship opportunities across Nigeria. 