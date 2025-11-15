package com.game.dto;

public class MoveRequestDTO {
    private Long gameId;
    private String direction;
    
    public MoveRequestDTO() {}
    
    public MoveRequestDTO(Long gameId, String direction) {
        this.gameId = gameId;
        this.direction = direction;
    }
    
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
