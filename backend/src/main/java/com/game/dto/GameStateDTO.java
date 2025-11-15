package com.game.dto;

public class GameStateDTO {
    private Long gameId;
    private int[][] board;
    private int score;
    private boolean gameOver;
    private int movesCount;
    private String message;
    
    public GameStateDTO() {}
    
    public GameStateDTO(Long gameId, int[][] board, int score, boolean gameOver, int movesCount, String message) {
        this.gameId = gameId;
        this.board = board;
        this.score = score;
        this.gameOver = gameOver;
        this.movesCount = movesCount;
        this.message = message;
    }
    
    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }
    
    public int[][] getBoard() { return board; }
    public void setBoard(int[][] board) { this.board = board; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    
    public int getMovesCount() { return movesCount; }
    public void setMovesCount(int movesCount) { this.movesCount = movesCount; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
