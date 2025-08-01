package com.internlinkng.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object NetworkUtils {
    private const val TAG = "NetworkUtils"
    private const val BASE_URL = "http://10.0.2.2:8080"
    
    suspend fun testConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
                
                val request = Request.Builder()
                    .url("$BASE_URL/hospitals")
                    .build()
                
                val response = client.newCall(request).execute()
                val isSuccess = response.isSuccessful
                
                Log.d(TAG, "Network test result: $isSuccess")
                isSuccess
            } catch (e: Exception) {
                Log.e(TAG, "Network test failed", e)
                false
            }
        }
    }
    
    suspend fun findWorkingEndpoint(): String? {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
            
            // Test different possible endpoints - prioritize emulator addresses
            val endpoints = listOf(
                "http://10.0.2.2:8080",  // Android emulator to host
                "http://10.0.3.2:8080",  // Genymotion emulator to host
                "http://10.230.79.90:8080", // Physical device IP
                "http://192.168.1.1:8080",
                "http://192.168.0.1:8080",
                "http://172.20.10.1:8080" // iPhone hotspot
            )
            
            for (endpoint in endpoints) {
                try {
                    val request = Request.Builder().url("$endpoint/hospitals").build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d(TAG, "Found working endpoint: $endpoint")
                        return@withContext endpoint
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Endpoint $endpoint failed: ${e.message}")
                }
            }
            
            Log.e(TAG, "No working endpoint found")
            null
        }
    }
    
    suspend fun scanNetworkForPhone(): List<String> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build()
            
            val possibleIPs = mutableListOf<String>()
            
            // Scan common phone IP ranges
            for (i in 1..254) {
                val ip = "10.230.79.$i"
                try {
                    val request = Request.Builder().url("http://$ip:8080/hospitals").build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        possibleIPs.add(ip)
                        Log.d(TAG, "Found device with backend at: $ip")
                    }
                } catch (e: Exception) {
                    // Ignore connection failures
                }
            }
            
            Log.d(TAG, "Network scan complete. Found ${possibleIPs.size} devices with backend")
            possibleIPs
        }
    }
    
    suspend fun testMultipleEndpoints(): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<String, Boolean>()
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
            
            // Test different endpoints - prioritize emulator addresses
            val endpoints = listOf(
                "http://10.0.2.2:8080/hospitals",  // Android emulator to host
                "http://10.0.3.2:8080/hospitals",  // Genymotion emulator to host
                "http://10.230.79.90:8080/hospitals", // Physical device IP
                "http://192.168.1.1:8080/hospitals" // Common router IP
            )
            
            endpoints.forEach { endpoint ->
                try {
                    val request = Request.Builder().url(endpoint).build()
                    val response = client.newCall(request).execute()
                    results[endpoint] = response.isSuccessful
                    Log.d(TAG, "Test $endpoint: ${response.isSuccessful}")
                } catch (e: Exception) {
                    results[endpoint] = false
                    Log.e(TAG, "Test $endpoint failed: ${e.message}")
                }
            }
            
            results
        }
    }
    
    fun getBaseUrl(): String = BASE_URL
    
    fun logNetworkInfo() {
        Log.d(TAG, "Base URL: $BASE_URL")
        Log.d(TAG, "Testing connection to backend...")
    }
} 