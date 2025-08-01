#!/bin/bash

echo "🚀 InternLinkNG Backend Deployment Script"
echo "=========================================="

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "❌ Git repository not found. Please initialize git and push to GitHub first."
    echo "Run these commands:"
    echo "  git init"
    echo "  git add ."
    echo "  git commit -m 'Initial commit'"
    echo "  git remote add origin <your-github-repo-url>"
    echo "  git push -u origin main"
    exit 1
fi

# Check if render.yaml exists
if [ ! -f "render.yaml" ]; then
    echo "❌ render.yaml not found. Please make sure you're in the backend directory."
    exit 1
fi

echo "✅ Git repository found"
echo "✅ render.yaml found"

echo ""
echo "📋 Next Steps:"
echo "1. Push your code to GitHub:"
echo "   git add ."
echo "   git commit -m 'Add deployment configuration'"
echo "   git push"
echo ""
echo "2. Go to https://render.com and sign up/login"
echo ""
echo "3. Click 'New +' and select 'Blueprint'"
echo ""
echo "4. Connect your GitHub repository"
echo ""
echo "5. Render will automatically detect the render.yaml and deploy both:"
echo "   - PostgreSQL database (internlinkng-db)"
echo "   - Web service (internlinkng-backend)"
echo ""
echo "6. Once deployed, your backend will be available at:"
echo "   https://internlinkng-backend.onrender.com"
echo ""
echo "7. Update your Android app's BASE_URL to point to the deployed backend"
echo ""
echo "🎉 Happy deploying!" 