# 2048 Game Service - Quick Start Guide

## Prerequisites
- Java 25 (installed)
- Maven 3.9.11 (installed)
- MySQL 8.0 (running on port 3306)
- Node.js 22.12.0 (installed)

## Backend Setup (Spring Boot)

### 1. Create MySQL Database
```bash
mysql -u root -p
CREATE DATABASE IF NOT EXISTS game2048_db;
EXIT;
```

### 2. Configure Database Password
Edit `backend/src/main/resources/application.properties` and update:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 3. Build Backend
```bash
cd backend
mvn clean install -DskipTests
```

### 4. Run Backend Server
```bash
mvn spring-boot:run
```
Or:
```bash
java -jar target/game2048-service-1.0.0.jar
```

Backend will start on: **http://localhost:8080**

## Frontend Setup (React)

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Run Frontend Development Server
```bash
npm run dev
```

Frontend will start on: **http://localhost:5173**

## Access the Game

Open your browser and navigate to: **http://localhost:5173**

## API Endpoints

### REST APIs
- `POST /api/game/start` - Start a new game
- `POST /api/game/move` - Make a move
- `GET /api/game/{id}` - Get game state
- `GET /api/leaderboard` - Get top players

### WebSocket
- **Endpoint**: `ws://localhost:8080/ws`
- **Destination**: `/app/game/move`
- **Subscribe**: `/topic/game/{sessionId}`

## Game Controls

### Desktop
- **Arrow Keys** or **WASD**: Move tiles
- **New Game Button**: Start a new game

### Mobile
- **Swipe**: Up, Down, Left, Right to move tiles
- Touch-friendly interface

## Features

✅ **Real-time Multiplayer** - WebSocket support for live updates
✅ **High Performance** - Optimized algorithms with O(n) complexity
✅ **Mobile Responsive** - Touch controls and GPU-accelerated animations
✅ **Database Persistence** - MySQL with HikariCP connection pooling (20 connections)
✅ **Leaderboard** - Track high scores and top players
✅ **RESTful API** - Full CRUD operations
✅ **Docker Ready** - Includes Dockerfile and docker-compose.yml

## Troubleshooting

### Backend won't start
1. Check MySQL is running: `netstat -an | findstr 3306`
2. Verify database credentials in `application.properties`
3. Check if port 8080 is available

### Frontend won't start
1. Clear npm cache: `npm cache clean --force`
2. Delete node_modules and reinstall: `rm -rf node_modules && npm install`
3. Check if port 5173 (or 3000) is available

### Build errors
- Java 25 is installed but project targets Java 21 for Spring Boot compatibility
- If you see "class file version 69" errors, the pom.xml is already configured to target Java 21

## Performance Notes

- **Concurrent Players**: Supports many simultaneous players
- **Database Pool**: 20 max connections, 5 minimum idle
- **WebSocket**: Real-time updates without polling
- **Caching**: Simple in-memory cache (Redis optional)
- **GPU Acceleration**: CSS transforms for smooth animations

## Architecture

```
backend/
├── src/main/java/com/game/
│   ├── model/          # JPA entities (Player, GameSession)
│   ├── repository/     # Spring Data JPA repositories
│   ├── service/        # Business logic (GameService)
│   ├── controller/     # REST endpoints
│   └── config/         # WebSocket & CORS configuration

frontend/
├── src/
│   ├── components/     # React components (GameBoard, Leaderboard)
│   ├── services/       # API clients (gameService, leaderboardService)
│   └── styles/         # CSS with GPU-accelerated animations
```

## Next Steps

1. **Deploy to Production**: Use provided Dockerfiles
2. **Enable Redis**: Uncomment Redis dependencies for better caching
3. **Add Authentication**: Integrate Spring Security
4. **Monitoring**: Add Spring Actuator endpoints
5. **Load Testing**: Test with multiple concurrent players

---

**Built with:** Java 25, Spring Boot 3.4.0, MySQL 8.0, React 18, Vite, WebSocket (STOMP)
