# Backend Deployment Guide

## Deploying to Render (Free Tier)

### Step 1: Create Render Account
1. Go to [render.com](https://render.com)
2. Sign up for a free account
3. Verify your email

### Step 2: Set up PostgreSQL Database
1. In your Render dashboard, click "New +"
2. Select "PostgreSQL"
3. Choose "Free" plan
4. Name it: `internlinkng-db`
5. Click "Create Database"
6. Note down the connection details (you'll need these later)

### Step 3: Deploy the Backend
1. In your Render dashboard, click "New +"
2. Select "Web Service"
3. Connect your GitHub repository (you'll need to push this code to GitHub first)
4. Configure the service:
   - **Name**: `internlinkng-backend`
   - **Environment**: `Docker`
   - **Branch**: `main`
   - **Root Directory**: `internlinkng-backend`
   - **Build Command**: Leave empty (uses Dockerfile)
   - **Start Command**: Leave empty (uses Dockerfile)

### Step 4: Configure Environment Variables
In your web service settings, add these environment variables:

```
DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db-name
DATABASE_USER=your-db-user
DATABASE_PASSWORD=your-db-password
JWT_SECRET=your-super-secret-jwt-key-here
```

Replace the values with your actual PostgreSQL connection details from Step 2.

### Step 5: Update Application Configuration
The application will automatically use environment variables if available, falling back to the local configuration.

### Step 6: Deploy
1. Click "Create Web Service"
2. Wait for the build to complete (usually 5-10 minutes)
3. Your backend will be available at: `https://your-app-name.onrender.com`

### Step 7: Update Android App
Once deployed, update your Android app's base URL to point to your Render deployment:

```kotlin
// In your Android app's network configuration
const val BASE_URL = "https://your-app-name.onrender.com"
```

## Testing the Deployment

1. Visit your deployment URL to see "InternLinkNG Backend is running!"
2. Test the API endpoints:
   - `GET /hospitals` - Should return hospital data
   - `POST /auth/login` - Should handle authentication

## Troubleshooting

- **Build fails**: Check the build logs in Render dashboard
- **Database connection fails**: Verify your environment variables
- **App can't connect**: Make sure your Android app is using the correct base URL

## Free Tier Limitations

- Render free tier has 750 hours/month
- Services sleep after 15 minutes of inactivity
- First request after sleep may take 30-60 seconds
- PostgreSQL free tier has 1GB storage and 90 days retention 