# 2048 Game Service

[![Deploy to Netlify](https://www.netlify.com/img/deploy/button.svg)](https://app.netlify.com/start/deploy?repository=https://github.com/mouryaG-TechQuest/2048Game)

A high-performance multiplayer 2048 game built with Spring Boot, MySQL, and React. Supports real-time gameplay with WebSocket for multiple concurrent players.

> **🚀 Quick Deploy**: See [DEPLOY_QUICK.md](./DEPLOY_QUICK.md) for a fast deployment guide or [NETLIFY_DEPLOYMENT.md](./NETLIFY_DEPLOYMENT.md) for detailed instructions.

## 🎮 Features

- **Fast & Responsive**: Optimized game logic with O(n) complexity for instant moves
- **Multiplayer Support**: WebSocket-based real-time updates for multiple players
- **Leaderboard**: Track top players and personal best scores
- **Mobile-Friendly**: Touch gestures support (swipe to play)
- **Performance Optimized**: 
  - Redis caching for fast game state retrieval
  - GPU-accelerated CSS transforms for smooth animations
  - Optimized database queries with HikariCP connection pooling
  - Efficient board manipulation algorithms

## 🏗️ Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL with JPA/Hibernate
- **Caching**: Redis for high-performance game state caching
- **WebSocket**: STOMP protocol for real-time updates
- **Connection Pool**: HikariCP for optimized database connections

### Frontend (React)
- **Framework**: React 18 with Vite
- **Styling**: CSS with GPU-accelerated transforms
- **Real-time**: WebSocket (SockJS + STOMP)
- **API**: Axios for REST calls

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+ and npm

## 🚀 Getting Started

### 1. Setup MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE game2048_db;
```

### 2. Setup Redis

```bash
# Start Redis server
redis-server

# Or using Docker
docker run -d -p 6379:6379 redis:latest
```

### 3. Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
# Update MySQL credentials if needed
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# Redis configuration (if not localhost)
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. Run Backend

```bash
cd backend
mvnw clean install
mvnw spring-boot:run
```

Backend will start on http://localhost:8080

### 5. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will start on http://localhost:3000

## 🎯 How to Play

### Desktop
- Use **Arrow Keys** (↑ ↓ ← →) or **WASD** to move tiles
- Combine tiles with the same number to create larger ones
- Try to reach the 2048 tile!

### Mobile/Tablet
- **Swipe** in any direction to move tiles
- Combine tiles with the same number
- Reach 2048 and beyond!

## 📡 API Endpoints

### Game Endpoints
- `POST /api/game/start?username={username}` - Start a new game
- `POST /api/game/move` - Make a move (direction: UP, DOWN, LEFT, RIGHT)
- `GET /api/game/{gameId}` - Get current game state

### Leaderboard Endpoints
- `GET /api/leaderboard/top?limit={limit}` - Get top players
- `GET /api/leaderboard/player/{username}` - Get player statistics

### WebSocket
- Connect to: `ws://localhost:8080/ws-game`
- Subscribe to: `/topic/game/{gameId}`
- Send moves to: `/app/game/move`

## ⚡ Performance Optimizations

### Backend
- **Optimized Game Logic**: O(n) time complexity for all move operations
- **Redis Caching**: Game states cached for instant retrieval
- **Connection Pooling**: HikariCP with max 20 connections
- **Efficient Serialization**: Fast JSON board state conversion
- **Database Indexing**: Optimized queries on username and scores

### Frontend
- **GPU Acceleration**: CSS `will-change` and `transform: translateZ(0)`
- **Debounced Moves**: Prevents rapid repeated moves
- **Virtual DOM**: React's efficient rendering
- **Lazy Animations**: 100-150ms transitions for smooth gameplay
- **Touch Optimization**: `touch-action: none` for mobile

## 📊 Database Schema

### Players Table
- `id` - Primary key
- `username` - Unique username
- `highest_score` - Best score achieved
- `total_games` - Total games played
- `created_at` - Account creation date
- `last_played` - Last activity timestamp

### Game Sessions Table
- `id` - Primary key
- `player_id` - Foreign key to players
- `current_score` - Current game score
- `board_state` - JSON representation of the board
- `status` - ACTIVE, COMPLETED, or GAME_OVER
- `moves_count` - Number of moves made
- `started_at` - Game start time
- `ended_at` - Game end time

## 🔧 Configuration

### Backend Port
Change in `application.properties`:
```properties
server.port=8080
```

### Frontend Port
Change in `vite.config.js`:
```javascript
server: {
  port: 3000
}
```

### CORS Settings
Update allowed origins in `CorsConfig.java` and `WebSocketConfig.java`

## 🐳 Docker Deployment (Optional)

```bash
# Build backend
cd backend
docker build -t game2048-backend .

# Build frontend
cd frontend
docker build -t game2048-frontend .

# Run with Docker Compose
docker-compose up
```

## 🌐 Netlify Deployment

Deploy the frontend to Netlify for free hosting! See the complete guide: **[NETLIFY_DEPLOYMENT.md](./NETLIFY_DEPLOYMENT.md)**

Quick steps:
1. Deploy backend to Heroku/Railway/Render
2. Connect repository to Netlify
3. Set environment variables (`VITE_API_BASE_URL`, `VITE_WS_URL`)
4. Deploy automatically with included `netlify.toml` configuration

For detailed instructions, troubleshooting, and best practices, check the [Netlify Deployment Guide](./NETLIFY_DEPLOYMENT.md).

## 🔍 Troubleshooting

### Backend Issues
- **Connection refused**: Ensure MySQL and Redis are running
- **Port 8080 in use**: Change server.port in application.properties
- **Database errors**: Verify MySQL credentials and database exists

### Frontend Issues
- **Cannot connect to backend**: Check if backend is running on port 8080
- **WebSocket errors**: Verify CORS settings and WebSocket endpoint
- **Build errors**: Run `npm install` to ensure all dependencies are installed

## 📈 Scaling for Production

### Backend Scaling
- Deploy multiple Spring Boot instances behind a load balancer
- Use external Redis cluster for shared caching
- Configure MySQL master-slave replication
- Enable Spring Boot Actuator for monitoring

### Frontend Scaling
- Build production version: `npm run build`
- Serve static files via CDN (CloudFront, Cloudflare)
- Enable GZIP compression
- Implement service workers for offline support

## 🎮 Game Rules

1. Start with two random tiles (2 or 4)
2. Move all tiles in one direction (up, down, left, right)
3. When two tiles with the same number touch, they merge into one
4. After each move, a new tile (2 or 4) appears randomly
5. The game ends when no moves are possible
6. Goal: Reach the 2048 tile (or higher!)

## 📝 License

MIT License - Feel free to use this project for learning or commercial purposes.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Support

For issues or questions, please open an issue on GitHub.

---

**Enjoy playing 2048! 🎉**
