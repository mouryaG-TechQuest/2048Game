package com.game.controller;

import com.game.dto.GameStateDTO;
import com.game.dto.MoveRequestDTO;
import com.game.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Create new game session
     */
    @PostMapping("/start")
    public ResponseEntity<GameStateDTO> startGame(@RequestParam String username) {
        GameStateDTO gameState = gameService.createNewGame(username);
        return ResponseEntity.ok(gameState);
    }
    
    /**
     * Process move (REST endpoint)
     */
    @PostMapping("/move")
    public ResponseEntity<GameStateDTO> makeMove(@RequestBody MoveRequestDTO moveRequest) {
        GameStateDTO gameState = gameService.processMove(
            moveRequest.getGameId(), 
            moveRequest.getDirection()
        );
        
        // Broadcast to WebSocket subscribers for real-time updates
        messagingTemplate.convertAndSend(
            "/topic/game/" + moveRequest.getGameId(), 
            gameState
        );
        
        return ResponseEntity.ok(gameState);
    }
    
    /**
     * Get current game state
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable Long gameId) {
        GameStateDTO gameState = gameService.getGameState(gameId);
        return ResponseEntity.ok(gameState);
    }
    
    /**
     * WebSocket endpoint for real-time move processing
     */
    @MessageMapping("/game/move")
    @SendTo("/topic/game/updates")
    public GameStateDTO processMoveViaWebSocket(MoveRequestDTO moveRequest) {
        GameStateDTO gameState = gameService.processMove(
            moveRequest.getGameId(), 
            moveRequest.getDirection()
        );
        
        // Also send to specific game topic
        messagingTemplate.convertAndSend(
            "/topic/game/" + moveRequest.getGameId(), 
            gameState
        );
        
        return gameState;
    }
}
