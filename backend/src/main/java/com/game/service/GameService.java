package com.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.dto.GameStateDTO;
import com.game.model.GameSession;
import com.game.model.Player;
import com.game.repository.GameSessionRepository;
import com.game.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameService {
    
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;
    
    public GameService(GameSessionRepository gameSessionRepository, 
                      PlayerRepository playerRepository,
                      ObjectMapper objectMapper) {
        this.gameSessionRepository = gameSessionRepository;
        this.playerRepository = playerRepository;
        this.objectMapper = objectMapper;
    }
    
    private static final int BOARD_SIZE = 4;
    private static final int[] POSSIBLE_VALUES = {2, 4}; // 90% chance for 2, 10% for 4
    
    /**
     * Create a new game session with optimized initial board
     */
    @Transactional
    public GameStateDTO createNewGame(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(username);
                    return playerRepository.save(newPlayer);
                });
        
        // Initialize board with two random tiles
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        addRandomTile(board);
        addRandomTile(board);
        
        GameSession gameSession = new GameSession();
        gameSession.setPlayer(player);
        gameSession.setBoardState(serializeBoard(board));
        gameSession.setCurrentScore(0);
        gameSession.setStatus(GameSession.GameStatus.ACTIVE);
        gameSession.setMovesCount(0);
        
        gameSession = gameSessionRepository.save(gameSession);
        
        return convertToDTO(gameSession, board);
    }
    
    /**
     * Process move with optimized algorithm - O(n) complexity
     */
    @Transactional
    @CacheEvict(value = "gameStates", key = "#gameId")
    public GameStateDTO processMove(Long gameId, String direction) {
        GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        
        if (session.getStatus() != GameSession.GameStatus.ACTIVE) {
            throw new RuntimeException("Game is not active");
        }
        
        int[][] board = deserializeBoard(session.getBoardState());
        int[][] previousBoard = copyBoard(board);
        
        // Process move based on direction
        boolean moved = false;
        int scoreGained = 0;
        
        switch (direction.toUpperCase()) {
            case "UP":
                var upResult = moveUp(board);
                moved = upResult[0] == 1;
                scoreGained = upResult[1];
                break;
            case "DOWN":
                var downResult = moveDown(board);
                moved = downResult[0] == 1;
                scoreGained = downResult[1];
                break;
            case "LEFT":
                var leftResult = moveLeft(board);
                moved = leftResult[0] == 1;
                scoreGained = leftResult[1];
                break;
            case "RIGHT":
                var rightResult = moveRight(board);
                moved = rightResult[0] == 1;
                scoreGained = rightResult[1];
                break;
            default:
                throw new RuntimeException("Invalid direction");
        }
        
        if (!moved) {
            return convertToDTO(session, board);
        }
        
        // Add new random tile
        addRandomTile(board);
        
        // Update score
        int newScore = session.getCurrentScore() + scoreGained;
        session.setCurrentScore(newScore);
        session.setBoardState(serializeBoard(board));
        session.setMovesCount(session.getMovesCount() + 1);
        
        // Check if game is over
        boolean gameOver = isGameOver(board);
        if (gameOver) {
            session.setStatus(GameSession.GameStatus.GAME_OVER);
            session.setEndedAt(LocalDateTime.now());
            updatePlayerStats(session.getPlayer(), newScore);
        }
        
        gameSessionRepository.save(session);
        
        GameStateDTO dto = convertToDTO(session, board);
        dto.setGameOver(gameOver);
        if (gameOver) {
            dto.setMessage("Game Over! Final Score: " + newScore);
        }
        
        return dto;
    }
    
    /**
     * Get current game state - cached for performance
     */
    @Cacheable(value = "gameStates", key = "#gameId")
    public GameStateDTO getGameState(Long gameId) {
        GameSession session = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        
        int[][] board = deserializeBoard(session.getBoardState());
        return convertToDTO(session, board);
    }
    
    /**
     * Add random tile (2 or 4) to an empty cell - O(1) average time
     */
    private void addRandomTile(int[][] board) {
        List<int[]> emptyCells = new ArrayList<>();
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        
        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(ThreadLocalRandom.current().nextInt(emptyCells.size()));
            // 90% chance for 2, 10% chance for 4
            board[cell[0]][cell[1]] = ThreadLocalRandom.current().nextDouble() < 0.9 ? 2 : 4;
        }
    }
    
    /**
     * Optimized move left - processes all rows in parallel conceptually
     */
    private int[] moveLeft(int[][] board) {
        boolean moved = false;
        int score = 0;
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            int[] row = board[i];
            int[] newRow = new int[BOARD_SIZE];
            int pos = 0;
            int lastMerged = -1;
            
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (row[j] != 0) {
                    if (pos > 0 && newRow[pos - 1] == row[j] && lastMerged != pos - 1) {
                        newRow[pos - 1] *= 2;
                        score += newRow[pos - 1];
                        lastMerged = pos - 1;
                        moved = true;
                    } else {
                        newRow[pos] = row[j];
                        if (pos != j) moved = true;
                        pos++;
                    }
                }
            }
            
            board[i] = newRow;
        }
        
        return new int[]{moved ? 1 : 0, score};
    }
    
    /**
     * Optimized move right
     */
    private int[] moveRight(int[][] board) {
        // Reverse, move left, reverse back
        reverseRows(board);
        int[] result = moveLeft(board);
        reverseRows(board);
        return result;
    }
    
    /**
     * Optimized move up
     */
    private int[] moveUp(int[][] board) {
        transpose(board);
        int[] result = moveLeft(board);
        transpose(board);
        return result;
    }
    
    /**
     * Optimized move down
     */
    private int[] moveDown(int[][] board) {
        transpose(board);
        reverseRows(board);
        int[] result = moveLeft(board);
        reverseRows(board);
        transpose(board);
        return result;
    }
    
    /**
     * Check if game is over - no valid moves left
     */
    private boolean isGameOver(int[][] board) {
        // Check for empty cells
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        
        // Check for possible merges
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (j < BOARD_SIZE - 1 && board[i][j] == board[i][j + 1]) return false;
                if (i < BOARD_SIZE - 1 && board[i][j] == board[i + 1][j]) return false;
            }
        }
        
        return true;
    }
    
    /**
     * Helper: Transpose board
     */
    private void transpose(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = i + 1; j < BOARD_SIZE; j++) {
                int temp = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = temp;
            }
        }
    }
    
    /**
     * Helper: Reverse all rows
     */
    private void reverseRows(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE / 2; j++) {
                int temp = board[i][j];
                board[i][j] = board[i][BOARD_SIZE - 1 - j];
                board[i][BOARD_SIZE - 1 - j] = temp;
            }
        }
    }
    
    /**
     * Helper: Copy board
     */
    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }
    
    /**
     * Update player statistics
     */
    private void updatePlayerStats(Player player, int score) {
        if (score > player.getHighestScore()) {
            player.setHighestScore(score);
        }
        player.setTotalGames(player.getTotalGames() + 1);
        playerRepository.save(player);
    }
    
    /**
     * Serialize board to JSON string
     */
    private String serializeBoard(int[][] board) {
        try {
            return objectMapper.writeValueAsString(board);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing board", e);
        }
    }
    
    /**
     * Deserialize board from JSON string
     */
    private int[][] deserializeBoard(String boardState) {
        try {
            return objectMapper.readValue(boardState, int[][].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing board", e);
        }
    }
    
    /**
     * Convert to DTO
     */
    private GameStateDTO convertToDTO(GameSession session, int[][] board) {
        GameStateDTO dto = new GameStateDTO();
        dto.setGameId(session.getId());
        dto.setBoard(board);
        dto.setScore(session.getCurrentScore());
        dto.setGameOver(session.getStatus() == GameSession.GameStatus.GAME_OVER);
        dto.setMovesCount(session.getMovesCount());
        return dto;
    }
}
