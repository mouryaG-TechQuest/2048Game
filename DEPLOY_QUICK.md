# 🚀 Quick Netlify Deploy

## Step 1: Deploy Backend
Choose one platform and deploy the Spring Boot backend:
- **Heroku**: https://www.heroku.com/ (recommended for beginners)
- **Railway**: https://railway.app/ 
- **Render**: https://render.com/

Note your backend URL (e.g., `https://your-app.herokuapp.com`)

## Step 2: Deploy Frontend to Netlify

### Via Netlify Dashboard (Easiest)
1. Go to https://app.netlify.com/
2. Click **"Add new site"** → **"Import an existing project"**
3. Connect your GitHub account and select this repository
4. Configure:
   - **Build command**: `cd frontend && npm install && npm run build`
   - **Publish directory**: `frontend/dist`
5. Add environment variables (under "Advanced"):
   - `VITE_API_BASE_URL` = `https://your-backend-url.com/api`
   - `VITE_WS_URL` = `https://your-backend-url.com/ws-game`
6. Click **"Deploy site"**

### Via Netlify CLI
```bash
npm install -g netlify-cli
netlify login
cd frontend
npm install && npm run build
netlify deploy --prod --dir=dist
```

## Step 3: Update Backend CORS
Add your Netlify URL to backend's allowed origins:
```java
// backend/src/main/java/com/game/config/CorsConfig.java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "https://your-site.netlify.app"  // Add this
));
```

Redeploy your backend after this change.

## Done! 🎉
Your game should now be live at your Netlify URL!

## Need More Help?
See the complete guide: [NETLIFY_DEPLOYMENT.md](./NETLIFY_DEPLOYMENT.md)
