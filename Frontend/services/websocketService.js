import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// WebSocket configuration
const WebSocketClient = (() => {
    let stompClient = null;

    // Connect to WebSocket
    const connect = (onMessageReceived, onConnected, onError) => {
        const socket = new SockJS('http://<your-backend-domain>:8080/ws'); // Replace with your backend's WebSocket URL
        stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str), // Optional debugging
            onConnect: () => {
                console.log('Connected to WebSocket');
                if (onConnected) onConnected();
            },
            onStompError: (error) => {
                console.error('WebSocket Error:', error);
                if (onError) onError(error);
            },
        });

        // Handle incoming messages
        stompClient.onReceive = (message) => {
            console.log('Message received:', message);
            if (onMessageReceived) onMessageReceived(message.body);
        };

        // Activate the client
        stompClient.activate();
    };

    // Subscribe to a destination
    const subscribe = (destination, callback) => {
        if (stompClient && stompClient.connected) {
            return stompClient.subscribe(destination, (message) => {
                callback(JSON.parse(message.body));
            });
        } else {
            console.error('WebSocket is not connected.');
        }
    };

    // Send a message
    const sendMessage = (destination, message) => {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination,
                body: JSON.stringify(message),
            });
        } else {
            console.error('WebSocket is not connected.');
        }
    };

    // Disconnect from WebSocket
    const disconnect = () => {
        if (stompClient) {
            stompClient.deactivate();
            console.log('Disconnected from WebSocket');
        }
    };

    return {
        connect,
        subscribe,
        sendMessage,
        disconnect,
    };
})();

export default WebSocketClient;
