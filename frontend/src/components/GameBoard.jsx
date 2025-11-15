import React, { useState, useEffect, useCallback, useRef } from 'react';
import { gameAPI } from '../api/gameApi';
import './GameBoard.css';

const GameBoard = () => {
  const [gameState, setGameState] = useState(null);
  const [username, setUsername] = useState(localStorage.getItem('username') || '');
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMoving, setIsMoving] = useState(false);
  const [message, setMessage] = useState('');
  const [playerRank, setPlayerRank] = useState(null);
  const [bestScore, setBestScore] = useState(null);
  const [showGameOver, setShowGameOver] = useState(false);
  const touchStartRef = useRef({ x: 0, y: 0 });

  // Resume game on mount if saved
  useEffect(() => {
    const savedGameId = localStorage.getItem('gameId');
    const savedBoard = localStorage.getItem('board');
    const savedScore = localStorage.getItem('score');
    const savedUsername = localStorage.getItem('username');

    if (savedGameId && savedBoard && savedScore && savedUsername) {
      setGameState({
        gameId: savedGameId,
        board: JSON.parse(savedBoard),
        score: parseInt(savedScore),
        gameOver: false,
        movesCount: 0
      });
      setUsername(savedUsername);
      setIsPlaying(true);
    }
  }, []);

  // Handle keyboard input
  useEffect(() => {
    const handleKeyDown = async (e) => {
      if (!isPlaying || isMoving || gameState?.gameOver) return;

      const keyMap = {
        'ArrowUp': 'UP',
        'ArrowDown': 'DOWN',
        'ArrowLeft': 'LEFT',
        'ArrowRight': 'RIGHT',
        'w': 'UP',
        's': 'DOWN',
        'a': 'LEFT',
        'd': 'RIGHT'
      };

      const direction = keyMap[e.key];
      if (direction) {
        e.preventDefault();
        await handleMove(direction);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isPlaying, isMoving, gameState]);

  // Handle touch gestures for mobile
  const handleTouchStart = (e) => {
    touchStartRef.current = {
      x: e.touches[0].clientX,
      y: e.touches[0].clientY
    };
  };

  const handleTouchEnd = async (e) => {
    if (!isPlaying || isMoving || gameState?.gameOver) return;

    const touchEnd = {
      x: e.changedTouches[0].clientX,
      y: e.changedTouches[0].clientY
    };

    const deltaX = touchEnd.x - touchStartRef.current.x;
    const deltaY = touchEnd.y - touchStartRef.current.y;
    const minSwipeDistance = 50;

    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      // Horizontal swipe
      if (Math.abs(deltaX) > minSwipeDistance) {
        await handleMove(deltaX > 0 ? 'RIGHT' : 'LEFT');
      }
    } else {
      // Vertical swipe
      if (Math.abs(deltaY) > minSwipeDistance) {
        await handleMove(deltaY > 0 ? 'DOWN' : 'UP');
      }
    }
  };

  const handleMove = async (direction) => {
    if (isMoving) return;
    
    setIsMoving(true);
    try {
      const updatedState = await gameAPI.makeMove(gameState.gameId, direction);
      setGameState(updatedState);
      
      // Save state to localStorage
      localStorage.setItem('board', JSON.stringify(updatedState.board));
      localStorage.setItem('score', updatedState.score.toString());
      
      if (updatedState.gameOver) {
        setMessage(updatedState.message || 'Game Over!');
        setShowGameOver(true);
        
        // Fetch player rank
        fetchPlayerRank();
      }
    } catch (error) {
      console.error('Error making move:', error);
    } finally {
      // Delay to allow animation to complete
      setTimeout(() => setIsMoving(false), 100);
    }
  };

  const startNewGame = async () => {
    if (!username.trim()) {
      setMessage('Please enter a username!');
      return;
    }

    try {
      const newGameState = await gameAPI.startGame(username);
      setGameState(newGameState);
      setIsPlaying(true);
      setMessage('');
      setShowGameOver(false);
      
      // Save to localStorage
      localStorage.setItem('username', username);
      localStorage.setItem('gameId', newGameState.gameId);
      localStorage.setItem('board', JSON.stringify(newGameState.board));
      localStorage.setItem('score', newGameState.score.toString());
    } catch (error) {
      setMessage('Error starting game. Please try again.');
      console.error('Error starting game:', error);
    }
  };

  const fetchPlayerRank = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/leaderboard/player/${username}/rank`);
      if (response.ok) {
        const data = await response.json();
        setPlayerRank(data.rank);
        setBestScore(data.bestScore);
      } else {
        setPlayerRank('Unranked');
        setBestScore(gameState?.score || 0);
      }
    } catch (error) {
      console.error('Error fetching rank:', error);
      setPlayerRank('N/A');
      setBestScore(gameState?.score || 0);
    }
  };

  const handleNewGame = () => {
    localStorage.removeItem('gameId');
    localStorage.removeItem('board');
    localStorage.removeItem('score');
    setShowGameOver(false);
    setPlayerRank(null);
    setBestScore(null);
    setMessage('');
    startNewGame();
  };

  const getTileColor = (value) => {
    const colors = {
      0: '#cdc1b4',
      2: '#eee4da',
      4: '#ede0c8',
      8: '#f2b179',
      16: '#f59563',
      32: '#f67c5f',
      64: '#f65e3b',
      128: '#edcf72',
      256: '#edcc61',
      512: '#edc850',
      1024: '#edc53f',
      2048: '#edc22e',
    };
    return colors[value] || '#3c3a32';
  };

  const getTileTextColor = (value) => {
    return value <= 4 ? '#776e65' : '#f9f6f2';
  };

  if (!isPlaying) {
    return (
      <div className="game-container">
        <div className="start-screen">
          <h1 className="game-title">2048 Game</h1>
          <p className="game-subtitle">Join the blocks to reach 2048!</p>
          <input
            type="text"
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && startNewGame()}
            className="username-input"
          />
          <button onClick={startNewGame} className="start-button">
            Start Game
          </button>
          {message && <p className="message error">{message}</p>}
          <div className="instructions">
            <p><strong>How to play:</strong></p>
            <p>Use arrow keys (↑ ↓ ← →) or WASD to move blocks</p>
            <p>On mobile: Swipe in any direction</p>
            <p>Combine blocks with the same number to create larger ones!</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="game-container">
      <div className="game-header">
        <div className="game-info">
          <h1 className="game-title">2048</h1>
          <p className="player-name">Player: {username}</p>
        </div>
        <div className="score-container">
          <div className="score-box">
            <div className="score-label">Score</div>
            <div className="score-value">{gameState?.score || 0}</div>
          </div>
          <div className="score-box">
            <div className="score-label">Moves</div>
            <div className="score-value">{gameState?.movesCount || 0}</div>
          </div>
          <button onClick={handleNewGame} className="new-game-button" title="Start a new game">
            New Game
          </button>
        </div>
      </div>

      <div 
        className="board-container"
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
      >
        <div className={`game-board ${isMoving ? 'moving' : ''}`}>
          {gameState?.board?.map((row, rowIndex) => (
            <div key={rowIndex} className="board-row">
              {row.map((cell, colIndex) => (
                <div
                  key={`${rowIndex}-${colIndex}`}
                  className={`tile ${cell !== 0 ? 'tile-active' : ''}`}
                  style={{
                    backgroundColor: getTileColor(cell),
                    color: getTileTextColor(cell),
                  }}
                >
                  {cell !== 0 && <span className="tile-value">{cell}</span>}
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>

      {gameState?.gameOver && showGameOver && (
        <div className="game-over-overlay">
          <div className="game-over-content">
            <h2>🎮 Game Over!</h2>
            <div className="game-stats">
              <p className="final-score">
                <strong>Your Score:</strong> {gameState.score}
              </p>
              {bestScore && gameState.score < bestScore && (
                <p className="best-score-display">
                  <strong>Your Best Score:</strong> {bestScore}
                </p>
              )}
              {playerRank && playerRank !== 'N/A' && playerRank !== 'Unranked' && (
                <div className="rank-display">
                  <p className="player-rank">
                    <strong>🏆 Your Rank:</strong> #{playerRank}
                  </p>
                  <p className="rank-note">Based on your best score{bestScore ? ` of ${bestScore}` : ''}</p>
                </div>
              )}
              {(playerRank === 'Unranked' || playerRank === 'N/A') && (
                <p className="unranked-message">
                  Play more games to get ranked on the leaderboard!
                </p>
              )}
              <p className="moves-count">
                <strong>Total Moves:</strong> {gameState.movesCount}
              </p>
            </div>
            <div className="motivational-text">
              {gameState.score >= 2048 ? (
                <p>🏆 Amazing! You reached 2048! Can you beat your own record?</p>
              ) : gameState.score >= 1024 ? (
                <p>💪 Great job! So close to 2048! Try again!</p>
              ) : bestScore && gameState.score < bestScore ? (
                <p>🎯 You can do better! Your best is {bestScore}. Keep trying!</p>
              ) : (
                <p>🎯 Keep playing to improve! Every game makes you better!</p>
              )}
            </div>
            <button onClick={handleNewGame} className="restart-button">
              🎮 Start New Game
            </button>
          </div>
        </div>
      )}

      <button onClick={() => setIsPlaying(false)} className="back-button">
        Back to Menu
      </button>

      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default GameBoard;
