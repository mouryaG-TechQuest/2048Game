import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const gameAPI = {
  // Start a new game
  startGame: async (username) => {
    const response = await axios.post(`${API_BASE_URL}/game/start?username=${username}`);
    return response.data;
  },

  // Make a move
  makeMove: async (gameId, direction) => {
    const response = await axios.post(`${API_BASE_URL}/game/move`, {
      gameId,
      direction
    });
    return response.data;
  },

  // Get game state
  getGameState: async (gameId) => {
    const response = await axios.get(`${API_BASE_URL}/game/${gameId}`);
    return response.data;
  },

  // Get leaderboard
  getLeaderboard: async (limit = 10) => {
    const response = await axios.get(`${API_BASE_URL}/leaderboard/top?limit=${limit}`);
    return response.data;
  },

  // Get player stats
  getPlayerStats: async (username) => {
    const response = await axios.get(`${API_BASE_URL}/leaderboard/player/${username}`);
    return response.data;
  }
};
