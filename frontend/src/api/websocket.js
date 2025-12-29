import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
  }

  connect(onGameUpdate) {
    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws-game';
    const socket = new SockJS(wsUrl);
    
    this.client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('STOMP: ' + str);
      },
      onConnect: () => {
        console.log('WebSocket Connected');
        this.connected = true;
      },
      onStompError: (frame) => {
        console.error('Broker error: ' + frame.headers['message']);
        console.error('Details: ' + frame.body);
      },
    });

    this.client.activate();
  }

  subscribeToGame(gameId, callback) {
    if (this.client && this.connected) {
      return this.client.subscribe(`/topic/game/${gameId}`, (message) => {
        const gameState = JSON.parse(message.body);
        callback(gameState);
      });
    }
  }

  sendMove(gameId, direction) {
    if (this.client && this.connected) {
      this.client.publish({
        destination: '/app/game/move',
        body: JSON.stringify({ gameId, direction })
      });
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.connected = false;
    }
  }
}

export default new WebSocketService();
