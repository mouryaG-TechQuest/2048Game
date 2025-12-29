# Deploying to Netlify

This guide will help you deploy the 2048 Game frontend to Netlify.

## Quick Start (TL;DR)

1. Deploy your backend (Spring Boot) to Heroku/Railway/Render
2. Go to [netlify.com](https://www.netlify.com/), sign in with GitHub
3. Click "Add new site" → Import your `2048Game` repository
4. Build settings:
   - Build command: `cd frontend && npm install && npm run build`
   - Publish directory: `frontend/dist`
5. Add environment variables:
   - `VITE_API_BASE_URL`: Your backend API URL
   - `VITE_WS_URL`: Your backend WebSocket URL
6. Click "Deploy site" and you're done! 🎉

**Note:** This repository includes `netlify.toml` which automatically configures most settings.

## Prerequisites

- A Netlify account (sign up at https://www.netlify.com/)
- A deployed backend API (the Spring Boot backend needs to be hosted separately)
- Git repository connected to GitHub

## Deployment Options

### Option 1: Deploy via Netlify UI (Recommended)

1. **Push your code to GitHub** (if not already done)

2. **Log in to Netlify**
   - Go to https://app.netlify.com/
   - Sign in with your GitHub account

3. **Import your repository**
   - Click "Add new site" → "Import an existing project"
   - Choose "GitHub" and authorize Netlify to access your repositories
   - Select the `2048Game` repository

4. **Configure build settings**
   - **Base directory**: Leave empty (or set to root)
   - **Build command**: `cd frontend && npm install && npm run build`
   - **Publish directory**: `frontend/dist`

5. **Set environment variables**
   - Click "Advanced" → "New variable"
   - Add the following environment variables:
     - `VITE_API_BASE_URL`: Your backend API URL (e.g., `https://your-backend.herokuapp.com/api`)
     - `VITE_WS_URL`: Your backend WebSocket URL (e.g., `https://your-backend.herokuapp.com/ws-game`)

6. **Deploy**
   - Click "Deploy site"
   - Wait for the build to complete (usually 2-3 minutes)
   - Your site will be live at a Netlify URL like `https://random-name-12345.netlify.app`

7. **Custom domain (Optional)**
   - Go to "Site settings" → "Domain management"
   - Click "Add custom domain" to use your own domain

### Option 2: Deploy via Netlify CLI

1. **Install Netlify CLI**
   ```bash
   npm install -g netlify-cli
   ```

2. **Login to Netlify**
   ```bash
   netlify login
   ```

3. **Build the frontend**
   ```bash
   cd frontend
   npm install
   npm run build
   ```

4. **Deploy**
   ```bash
   # From the root directory
   netlify deploy --dir=frontend/dist
   ```

5. **Deploy to production**
   ```bash
   netlify deploy --prod --dir=frontend/dist
   ```

### Option 3: One-Click Deploy with netlify.toml

This repository already includes a `netlify.toml` configuration file. Simply:

1. Connect your repository to Netlify
2. Set the environment variables in Netlify UI
3. Netlify will automatically use the configuration from `netlify.toml`

## Backend Deployment

**Important**: The backend (Spring Boot) needs to be deployed separately as Netlify only hosts static sites. You can deploy the backend to:

- **Heroku**: https://www.heroku.com/ (easy, good free tier)
- **Railway**: https://railway.app/ (modern, good free tier)
- **Render**: https://render.com/ (simple, automatic deployments)
- **AWS Elastic Beanstalk**: Enterprise-grade, more complex
- **Google Cloud Run**: Serverless, pay per use
- **Digital Ocean App Platform**: Simple, scalable

### Quick Backend Deployment with Heroku

1. **Install Heroku CLI**
   ```bash
   # macOS
   brew tap heroku/brew && brew install heroku
   
   # Windows
   # Download from https://devcenter.heroku.com/articles/heroku-cli
   ```

2. **Login and create app**
   ```bash
   heroku login
   cd backend
   heroku create your-2048-game-backend
   ```

3. **Add MySQL database**
   ```bash
   heroku addons:create jawsdb:kitefin
   ```

4. **Configure environment**
   ```bash
   # Heroku will automatically set DATABASE_URL
   # Update your application.properties to read from DATABASE_URL
   ```

5. **Deploy**
   ```bash
   git subtree push --prefix backend heroku main
   # Or use the Heroku Git remote
   ```

## Environment Variables Configuration

After deploying the backend, update your Netlify environment variables:

1. Go to Netlify dashboard → Your site → Site settings → Environment variables
2. Update or add:
   - `VITE_API_BASE_URL` = `https://your-2048-game-backend.herokuapp.com/api`
   - `VITE_WS_URL` = `https://your-2048-game-backend.herokuapp.com/ws-game`

3. Trigger a new deploy for changes to take effect

## CORS Configuration

Make sure your backend allows requests from your Netlify domain:

1. Update `backend/src/main/java/com/game/config/CorsConfig.java`
2. Add your Netlify URL to allowed origins:
   ```java
   configuration.setAllowedOrigins(Arrays.asList(
       "http://localhost:3000",
       "http://localhost:5173", 
       "https://your-site.netlify.app"  // Add your Netlify URL
   ));
   ```

3. Redeploy your backend

## Troubleshooting

### Build Fails on Netlify

- **Check Node version**: Netlify uses Node 18 by default (configured in netlify.toml)
- **Check build logs**: Look for specific error messages in the deploy log
- **Local build test**: Run `cd frontend && npm run build` locally to test

### API Calls Fail

- **CORS errors**: Make sure your backend allows requests from your Netlify domain
- **Wrong API URL**: Verify environment variables are set correctly in Netlify
- **Backend down**: Check if your backend service is running

### WebSocket Connection Fails

- **Protocol mismatch**: Ensure you're using `wss://` for HTTPS sites
- **CORS/WebSocket config**: Check backend WebSocket configuration
- **Backend limitations**: Some platforms have WebSocket limitations

### CSS/Assets Not Loading

- **Base path**: Verify `vite.config.js` doesn't have a custom base path
- **Cache issues**: Try hard refresh (Ctrl+Shift+R or Cmd+Shift+R)
- **CDN delay**: Wait a few minutes for Netlify CDN to propagate

## Monitoring and Analytics

1. **Netlify Analytics**
   - Enable in Site settings → Analytics
   - Track page views, performance, and more

2. **Backend Monitoring**
   - Use your backend platform's built-in monitoring
   - Consider adding Spring Boot Actuator for health checks

## Continuous Deployment

Once connected to GitHub, Netlify will automatically:
- Deploy on every push to your main branch
- Create deploy previews for pull requests
- Run the build command and deploy the result

## Cost Considerations

**Netlify (Frontend):**
- Free tier: 100 GB bandwidth/month, 300 build minutes/month
- Perfect for personal projects and small apps

**Backend Hosting:**
- Heroku: Free tier available (sleeps after 30 min inactivity)
- Railway: $5/month free credit
- Render: Free tier available (sleeps after inactivity)

## Next Steps

1. ✅ Deploy backend to chosen platform
2. ✅ Update CORS configuration on backend
3. ✅ Set environment variables in Netlify
4. ✅ Deploy frontend to Netlify
5. ✅ Test the deployed application
6. 🎯 Share your game with the world!

## Support

For Netlify-specific issues:
- Documentation: https://docs.netlify.com/
- Support: https://answers.netlify.com/

For application issues:
- Open an issue on GitHub
- Check the repository README for more information

---

**Happy Deploying! 🚀**
