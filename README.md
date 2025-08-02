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
- **Share Details**: Share hospital information via WhatsApp, Email, etc.

### For Administrators
- **Hospital Management**: Add, edit, and delete hospital listings
- **Application Monitoring**: View submitted applications
- **User Management**: Manage user accounts and permissions
- **Admin Dashboard**: Platform statistics and management tools

## 🛠 Tech Stack

### Frontend (Android)
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI framework
- **MVVM Architecture**: Clean separation of concerns
- **Firebase**: Backend-as-a-Service
- **Firebase Authentication**: User management
- **Firestore**: NoSQL database
- **Navigation Compose**: Screen navigation
- **Material Design 3**: Modern UI components

### Backend (Firebase)
- **Firebase Authentication**: Secure user sessions
- **Firestore**: Real-time NoSQL database
- **Firebase Storage**: File storage (future use)
- **Firebase Analytics**: User behavior tracking
- **Offline Persistence**: Automatic data caching

## 📱 Screens

1. **Login/Signup Screen**: User authentication with Firebase
2. **Home Screen**: Hospital listings with search and filters
3. **Hospital Details Screen**: Detailed hospital information and application
4. **Applied Hospitals Screen**: Track your applications
5. **Settings Screen**: User preferences and profile management
6. **Admin Screens**: Hospital management interface
   - Admin Home Dashboard
   - Add/Edit Hospital Form
   - Hospital Management List

## 🏗 Architecture

### MVVM Pattern
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel      │    │  Firebase       │
│   (Compose)     │◄──►│  (Business      │◄──►│  (Backend)      │
│                 │    │   Logic)        │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Data Flow
1. **UI** observes ViewModel's StateFlow
2. **ViewModel** processes user actions and updates state
3. **Firebase** provides real-time data and authentication
4. **Offline Persistence** ensures app works without internet
5. **Firestore** handles data synchronization

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.8+
- Android SDK 33+
- Java 11+
- Firebase project setup

### Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the project
3. Download `google-services.json` and place it in `app/`
4. Enable Authentication and Firestore in Firebase Console
5. Set up Firestore security rules

### Android App Setup
1. Open the project in Android Studio
2. Ensure `google-services.json` is in the `app/` directory
3. Build and run the app

## 📊 Database Schema

### Firestore Collections

#### Hospitals Collection
- `id`: Document ID
- `name`: Hospital name
- `state`: Nigerian state
- `professions`: Comma-separated list of medical professions
- `salaryRange`: Salary range for interns
- `deadline`: Application deadline
- `onlineApplication`: Boolean flag for online applications
- `applicationUrl`: URL for online applications
- `physicalAddress`: Hospital address
- `professionSalaries`: JSON string of profession-salary mappings
- `created`: Creation date
- `updated_at`: Last update timestamp

#### Users Collection
- `uid`: User ID from Firebase Auth
- `email`: User's email address
- `firstname`: User's first name
- `lastname`: User's last name
- `phone`: User's phone number
- `created_at`: Account creation timestamp

## 🔐 Authentication

The app supports two user types:
- **Regular Users**: Can browse hospitals and submit applications
- **Administrators**: Can manage hospital listings

### Demo Credentials
- **Admin**: `admin@internlinkng.com` / `Admin123!`
- **User**: Any email/password combination for signup

## 🌟 Key Features Implementation

### Search and Filter
- Real-time search across hospital names, states, and professions
- Filter chips for profession, state, and salary range
- Combined filtering with multiple criteria
- All Nigerian states included in filter options

### Offline Support
- Firebase offline persistence caches hospital data
- App works without internet connection
- Syncs data when connection is restored

### Application Management
- Mark hospitals as applied
- Track application status
- Submit digital applications
- View applied hospitals list

### Sharing Functionality
- Share hospital details via Android share intent
- Formatted text with emojis and structured information
- Support for WhatsApp, Telegram, Email, SMS, etc.

### Modern UI/UX
- Material Design 3 components
- Dark/Light theme support
- Responsive design
- Smooth animations and transitions
- Professional color scheme with medical theme

## 🔧 Configuration

### Firebase Configuration
1. Add your `google-services.json` to the `app/` directory
2. Configure Firestore security rules
3. Set up Firebase Authentication providers

### Android Configuration
The app automatically configures Firebase on startup:
```kotlin
// Firebase is initialized in NetworkUtils.kt
FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
    .build()
```

## 📈 Future Enhancements

- [ ] Push notifications for new hospital listings
- [ ] Real-time courier service integration
- [ ] Document upload for applications
- [ ] Interview scheduling system
- [ ] Analytics dashboard for administrators
- [ ] Multi-language support
- [ ] Advanced search with geolocation
- [ ] Profile picture upload to Firebase Storage
- [ ] Application submission with Firebase
- [ ] User management for admins

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
- **Backend**: Firebase (Authentication, Firestore, Storage)
- **Frontend**: Jetpack Compose + Kotlin

---

**InternLinkNG** - Connecting medical graduates with internship opportunities across Nigeria. 