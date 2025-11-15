package com.game.repository;

import com.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    Optional<Player> findByUsername(String username);
    
    @Query("SELECT p FROM Player p ORDER BY p.highestScore DESC")
    List<Player> findTopPlayersByScore();
    
    boolean existsByUsername(String username);
}
