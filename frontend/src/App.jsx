import React, { useState } from 'react';
import GameBoard from './components/GameBoard';
import Leaderboard from './components/Leaderboard';
import './App.css';

function App() {
  const [showLeaderboard, setShowLeaderboard] = useState(false);

  return (
    <div className="app">
      {!showLeaderboard ? (
        <>
          <GameBoard />
          <button 
            onClick={() => setShowLeaderboard(true)} 
            className="leaderboard-toggle"
          >
            View Leaderboard
          </button>
        </>
      ) : (
        <>
          <Leaderboard />
          <button 
            onClick={() => setShowLeaderboard(false)} 
            className="leaderboard-toggle"
          >
            Back to Game
          </button>
        </>
      )}
    </div>
  );
}

export default App;
