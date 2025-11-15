package com.game.controller;

import com.game.model.Player;
import com.game.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    
    private final PlayerRepository playerRepository;
    
    public LeaderboardController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    
    /**
     * Get top players by score
     */
    @GetMapping("/top")
    public ResponseEntity<List<Player>> getTopPlayers(@RequestParam(defaultValue = "10") int limit) {
        List<Player> topPlayers = playerRepository.findTopPlayersByScore();
        
        // Limit results
        if (topPlayers.size() > limit) {
            topPlayers = topPlayers.subList(0, limit);
        }
        
        return ResponseEntity.ok(topPlayers);
    }
    
    /**
     * Get player statistics
     */
    @GetMapping("/player/{username}")
    public ResponseEntity<Player> getPlayerStats(@PathVariable String username) {
        return playerRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get player rank based on their best score
     */
    @GetMapping("/player/{username}/rank")
    public ResponseEntity<PlayerRankResponse> getPlayerRank(@PathVariable String username) {
        return playerRepository.findByUsername(username)
                .map(player -> {
                    List<Player> allPlayers = playerRepository.findTopPlayersByScore();
                    int rank = 1;
                    for (Player p : allPlayers) {
                        if (p.getUsername().equals(username)) {
                            break;
                        }
                        rank++;
                    }
                    
                    PlayerRankResponse response = new PlayerRankResponse();
                    response.setUsername(player.getUsername());
                    response.setRank(rank);
                    response.setBestScore(player.getHighestScore());
                    response.setTotalGames(player.getTotalGames());
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Inner class for rank response
    public static class PlayerRankResponse {
        private String username;
        private int rank;
        private int bestScore;
        private int totalGames;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        
        public int getBestScore() { return bestScore; }
        public void setBestScore(int bestScore) { this.bestScore = bestScore; }
        
        public int getTotalGames() { return totalGames; }
        public void setTotalGames(int totalGames) { this.totalGames = totalGames; }
    }
}
