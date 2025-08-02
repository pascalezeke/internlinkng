# Supabase Setup Guide for InternLinkNG

## 🚀 Step 1: Create Supabase Project

1. Go to [supabase.com](https://supabase.com)
2. Click "Start your project"
3. Sign up/Login with GitHub or email
4. Click "New Project"
5. Fill in project details:
   - **Name**: `internlinkng`
   - **Database Password**: Create a strong password (save it!)
   - **Region**: Choose closest to your users
6. Click "Create new project"

## 📊 Step 2: Set up Database

1. **Go to SQL Editor** in your Supabase dashboard
2. **Copy and paste** the contents of `supabase-setup.sql`
3. **Click "Run"** to create tables and policies
4. **Copy and paste** the contents of `sample-data.sql`
5. **Click "Run"** to insert sample data

## 🔑 Step 3: Get API Keys

1. **Go to Settings** → **API** in your Supabase dashboard
2. **Copy these values**:
   - **Project URL** (e.g., `https://your-project.supabase.co`)
   - **Anon Key** (public key)
   - **Service Role Key** (private key - keep secret!)

## 📱 Step 4: Update Android App

### Add Supabase Dependencies

Add to your `app/build.gradle.kts`:

```kotlin
dependencies {
    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.4.7")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.4.7")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.4.7")
    
    // JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}
```

### Create Supabase Client

Create `app/src/main/java/com/internlinkng/data/SupabaseClient.kt`:

```kotlin
package com.internlinkng.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "YOUR_PROJECT_URL"
    private const val SUPABASE_ANON_KEY = "YOUR_ANON_KEY"
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
    }
    
    val auth = client.auth
    val database = client.postgrest
    val storage = client.storage
}
```

### Update NetworkUtils

Replace your current `NetworkUtils.kt` with Supabase calls:

```kotlin
package com.internlinkng.data

import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkUtils {
    suspend fun getHospitals(): List<Hospital> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseClient.database
                .from("hospitals")
                .select(Columns.all())
                .decodeList<Hospital>()
            response
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = SupabaseClient.auth.signInWith(email, password)
                AuthResult.Success(response.user)
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }
}
```

## 🔐 Step 5: Authentication

Supabase handles authentication automatically. Users can:
- Sign up with email/password
- Sign in with email/password
- Reset passwords
- Update profiles

## 📊 Step 6: Test Your Setup

1. **Run the Android app**
2. **Try logging in** with test credentials
3. **Check if hospitals load** from Supabase
4. **Test admin features** (if you have admin access)

## 🎯 Benefits of Supabase

✅ **No server management** - Everything is handled
✅ **Real-time updates** - Data changes instantly
✅ **Built-in authentication** - Secure user management
✅ **Admin dashboard** - Manage data visually
✅ **Automatic scaling** - Handles traffic automatically
✅ **Free tier** - Generous limits for development

## 🚀 Next Steps

1. **Replace your current backend** with Supabase calls
2. **Test all features** thoroughly
3. **Deploy to production** when ready
4. **Monitor usage** in Supabase dashboard

Your app will be live and working in minutes! 🎯 