package com.game.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
public class GameSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Column(name = "current_score")
    private Integer currentScore = 0;
    
    @Column(name = "board_state", length = 2000)
    private String boardState;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status = GameStatus.ACTIVE;
    
    @Column(name = "moves_count")
    private Integer movesCount = 0;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    public GameSession() {}
    
    public GameSession(Long id, Player player, Integer currentScore, String boardState,
                      GameStatus status, Integer movesCount, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.id = id;
        this.player = player;
        this.currentScore = currentScore;
        this.boardState = boardState;
        this.status = status;
        this.movesCount = movesCount;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
    
    public enum GameStatus {
        ACTIVE, COMPLETED, GAME_OVER
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    
    public Integer getCurrentScore() { return currentScore; }
    public void setCurrentScore(Integer currentScore) { this.currentScore = currentScore; }
    
    public String getBoardState() { return boardState; }
    public void setBoardState(String boardState) { this.boardState = boardState; }
    
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    
    public Integer getMovesCount() { return movesCount; }
    public void setMovesCount(Integer movesCount) { this.movesCount = movesCount; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
}
