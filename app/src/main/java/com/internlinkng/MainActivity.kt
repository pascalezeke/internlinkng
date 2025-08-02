package com.internlinkng

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.internlinkng.data.local.AppDatabase
import com.internlinkng.data.remote.ApiService
import com.internlinkng.data.remote.AuthInterceptor
import com.internlinkng.ui.navigation.AppNavigation
import com.internlinkng.ui.theme.InternsTheme
import com.internlinkng.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize dependencies
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "internlinkng_database"
        ).addMigrations(AppDatabase.MIGRATION_1_2)
         .fallbackToDestructiveMigration()
         .build()

        val hospitalDao = database.hospitalDao()

        // Initialize Retrofit with AuthInterceptor (keeping for compatibility)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        
        Log.d(TAG, "App initialized with Supabase backend")

        setContent {
            InternsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create ViewModel with dependencies
                    val viewModel: MainViewModel = viewModel {
                        MainViewModel(apiService, hospitalDao)
                    }
                    
                    // Navigation
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}