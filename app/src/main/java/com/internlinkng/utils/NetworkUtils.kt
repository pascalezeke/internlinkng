package com.internlinkng.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.internlinkng.data.model.Hospital
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object NetworkUtils {
    private const val TAG = "NetworkUtils"
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Configure Firebase for global access
    init {
        try {
            // Enable offline persistence for better connectivity
            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            
            Log.d(TAG, "Firebase configured for global access with offline persistence")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure Firebase settings", e)
        }
    }
    
    suspend fun getHospitals(): List<Hospital> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching hospitals from Firebase...")
            val snapshot = firestore.collection("hospitals").get().await()
            val hospitals = snapshot.documents.mapNotNull { doc ->
                try {
                    Hospital(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        state = doc.getString("state") ?: "",
                        professions = doc.getString("professions") ?: "",
                        salaryRange = doc.getString("salary_range") ?: "",
                        deadline = doc.getString("deadline") ?: "",
                        created = doc.getString("created") ?: "",
                        onlineApplication = doc.getBoolean("online_application") ?: false,
                        applicationUrl = doc.getString("application_url"),
                        physicalAddress = doc.getString("physical_address") ?: "",
                        professionSalaries = doc.getString("profession_salaries"),
                        createdAt = null, // Don't parse timestamp as string
                        updatedAt = null  // Don't parse timestamp as string
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing hospital document ${doc.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "Successfully fetched ${hospitals.size} hospitals from Firebase")
            hospitals
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch hospitals from Firebase", e)
            emptyList()
        }
    }
    
    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting login with Firebase...")
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Login successful for user: ${result.user?.email}")
                AuthResult.Success(result.user)
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }
    
    suspend fun signUp(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting signup with Firebase...")
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Signup successful for user: ${result.user?.email}")
                AuthResult.Success(result.user)
            } catch (e: Exception) {
                Log.e(TAG, "Signup failed", e)
                AuthResult.Error(e.message ?: "Signup failed")
            }
        }
    }
    
    suspend fun logout(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Logging out from Firebase...")
                auth.signOut()
                Log.d(TAG, "Logout successful")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
                false
            }
        }
    }
    
    suspend fun getCurrentUser(): com.google.firebase.auth.FirebaseUser? {
        return withContext(Dispatchers.IO) {
            try {
                auth.currentUser
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get current user", e)
                null
            }
        }
    }
    
    fun isLoggedIn(): Boolean {
        return try {
            auth.currentUser != null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check login status", e)
            false
        }
    }
    
    suspend fun testFirebaseConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Testing Firebase connection...")
                // Try to access Firestore to test connectivity
                val testDoc = firestore.collection("test").document("connection")
                testDoc.get().await()
                Log.d(TAG, "Firebase connection test: SUCCESS")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Firebase connection test: FAILED", e)
                Log.e(TAG, "Error details: ${e.message}")
                
                // Check if it's a regional/network issue
                when {
                    e.message?.contains("network", ignoreCase = true) == true -> {
                        Log.e(TAG, "Network connectivity issue detected")
                    }
                    e.message?.contains("permission", ignoreCase = true) == true -> {
                        Log.e(TAG, "Firebase permission issue detected")
                    }
                    e.message?.contains("timeout", ignoreCase = true) == true -> {
                        Log.e(TAG, "Connection timeout detected")
                    }
                    else -> {
                        Log.e(TAG, "Unknown Firebase connection issue")
                    }
                }
                false
            }
        }
    }
    
    suspend fun testGlobalConnectivity(): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<String, Boolean>()
            
            // Test Firebase connection
            results["firebase"] = try {
                val testDoc = firestore.collection("test").document("connection")
                testDoc.get().await()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Firebase connectivity test failed", e)
                false
            }
            
            // Test basic internet connectivity
            results["internet"] = try {
                val url = java.net.URL("https://www.google.com")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "HEAD"
                val responseCode = connection.responseCode
                responseCode == 200
            } catch (e: Exception) {
                Log.e(TAG, "Internet connectivity test failed", e)
                false
            }
            
            Log.d(TAG, "Global connectivity test results: $results")
            results
        }
    }
    
    suspend fun getHospitalsWithFallback(): List<Hospital> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching hospitals from Firebase...")
            val hospitals = getHospitals()
            if (hospitals.isNotEmpty()) {
                Log.d(TAG, "Successfully fetched ${hospitals.size} hospitals from Firebase")
                hospitals
            } else {
                Log.w(TAG, "No hospitals found in Firebase, returning empty list")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch hospitals, returning empty list", e)
            emptyList()
        }
    }
}

sealed class AuthResult {
    data class Success(val user: com.google.firebase.auth.FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
} 