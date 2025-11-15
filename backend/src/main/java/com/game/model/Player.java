package com.game.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "highest_score")
    private Integer highestScore = 0;
    
    @Column(name = "total_games")
    private Integer totalGames = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_played")
    private LocalDateTime lastPlayed;
    
    public Player() {}
    
    public Player(Long id, String username, Integer highestScore, Integer totalGames, 
                  LocalDateTime createdAt, LocalDateTime lastPlayed) {
        this.id = id;
        this.username = username;
        this.highestScore = highestScore;
        this.totalGames = totalGames;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastPlayed = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastPlayed = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Integer getHighestScore() { return highestScore; }
    public void setHighestScore(Integer highestScore) { this.highestScore = highestScore; }
    
    public Integer getTotalGames() { return totalGames; }
    public void setTotalGames(Integer totalGames) { this.totalGames = totalGames; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastPlayed() { return lastPlayed; }
    public void setLastPlayed(LocalDateTime lastPlayed) { this.lastPlayed = lastPlayed; }
}
