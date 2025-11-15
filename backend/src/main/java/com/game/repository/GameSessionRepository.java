package com.game.repository;

import com.game.model.GameSession;
import com.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    
    List<GameSession> findByPlayerAndStatus(Player player, GameSession.GameStatus status);
    
    @Query("SELECT g FROM GameSession g WHERE g.player.id = :playerId ORDER BY g.startedAt DESC")
    List<GameSession> findRecentGamesByPlayerId(Long playerId);
    
    Optional<GameSession> findByIdAndStatus(Long id, GameSession.GameStatus status);
}
