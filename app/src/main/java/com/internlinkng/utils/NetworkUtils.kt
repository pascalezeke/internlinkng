package com.internlinkng.utils

import android.util.Log
import com.internlinkng.data.model.Hospital
import com.internlinkng.data.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetworkUtils {
    private const val TAG = "NetworkUtils"
    
    suspend fun getHospitals(): List<Hospital> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching hospitals from Supabase...")
            val response = SupabaseClient.database
                .from("hospitals")
                .select()
                .decodeList<Hospital>()
            
            Log.d(TAG, "Successfully fetched ${response.size} hospitals from Supabase")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch hospitals from Supabase", e)
            emptyList()
        }
    }
    
    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting login with Supabase...")
                val response = SupabaseClient.auth.signInWith(email, password)
                Log.d(TAG, "Login successful for user: ${response.user?.email}")
                AuthResult.Success(response.user)
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }
    
    suspend fun signUp(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting signup with Supabase...")
                val response = SupabaseClient.auth.signUpWith(email, password)
                Log.d(TAG, "Signup successful for user: ${response.user?.email}")
                AuthResult.Success(response.user)
            } catch (e: Exception) {
                Log.e(TAG, "Signup failed", e)
                AuthResult.Error(e.message ?: "Signup failed")
            }
        }
    }
    
    suspend fun logout(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Logging out from Supabase...")
                SupabaseClient.auth.signOut()
                Log.d(TAG, "Logout successful")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
                false
            }
        }
    }
    
    suspend fun getCurrentUser(): io.github.jan.supabase.gotrue.user.UserInfo? {
        return withContext(Dispatchers.IO) {
            try {
                SupabaseClient.auth.currentUserOrNull()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get current user", e)
                null
            }
        }
    }
    
    fun isLoggedIn(): Boolean {
        return try {
            SupabaseClient.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check login status", e)
            false
        }
    }
}

sealed class AuthResult {
    data class Success(val user: io.github.jan.supabase.gotrue.user.UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
} 