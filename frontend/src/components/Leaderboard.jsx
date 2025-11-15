import React, { useState, useEffect } from 'react';
import { gameAPI } from '../api/gameApi';
import './Leaderboard.css';

const Leaderboard = () => {
  const [topPlayers, setTopPlayers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLeaderboard();
  }, []);

  const fetchLeaderboard = async () => {
    try {
      const players = await gameAPI.getLeaderboard(10);
      setTopPlayers(players);
    } catch (error) {
      console.error('Error fetching leaderboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading leaderboard...</div>;
  }

  return (
    <div className="leaderboard-container">
      <h2 className="leaderboard-title">🏆 Top Players</h2>
      <div className="leaderboard-list">
        {topPlayers.length === 0 ? (
          <p className="no-players">No players yet. Be the first!</p>
        ) : (
          topPlayers.map((player, index) => (
            <div key={player.id} className={`leaderboard-item rank-${index + 1}`}>
              <div className="rank">{index + 1}</div>
              <div className="player-info">
                <div className="username">{player.username}</div>
                <div className="stats">
                  <span>Best: {player.highestScore}</span>
                  <span>Games: {player.totalGames}</span>
                </div>
              </div>
              <div className="score">{player.highestScore}</div>
            </div>
          ))
        )}
      </div>
      <button onClick={fetchLeaderboard} className="refresh-button">
        Refresh
      </button>
    </div>
  );
};

export default Leaderboard;
