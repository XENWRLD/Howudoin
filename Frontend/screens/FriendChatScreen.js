import React, { useState, useEffect, useRef } from 'react';
import { View, TextInput, FlatList, Text, Button, StyleSheet, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';

// Decode Base64 for JWT tokens
const decodeBase64 = (base64) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  let str = '';
  let bytes = 0;
  let buffer = 0;
  let i = 0;

  base64 = base64.replace(/=+$/, '');

  while (i < base64.length) {
    buffer = (buffer << 6) | chars.indexOf(base64.charAt(i++));
    bytes += 6;

    while (bytes >= 8) {
      str += String.fromCharCode((buffer >> (bytes - 8)) & 0xff);
      bytes -= 8;
    }
  }

  return str;
};

// Decode JWT token
const decodeToken = (token) => {
  try {
    if (!token || !token.includes('.')) {
      console.error('Token is invalid or improperly formatted:', token);
      throw new Error('Invalid token format');
    }

    const base64Url = token.split('.')[1]; // Extract the payload part of the JWT
    console.log('Base64 payload part:', base64Url);

    if (!base64Url) {
      console.error('Token does not contain a payload:', token);
      throw new Error('Token missing payload');
    }

    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const decodedPayload = JSON.parse(decodeBase64(base64)); // Decode Base64 to JSON
    console.log('Decoded token payload:', decodedPayload);
    return decodedPayload;
  } catch (error) {
    console.error('Error decoding token:', error.message);
    return null;
  }
};

export default function FriendChatScreen({ route }) {
  const { friendId, friendName } = route.params;
  const [messages, setMessages] = useState([]);
  const [messageText, setMessageText] = useState('');
  const [userId, setUserId] = useState(null);
  const stompClient = useRef(null);

  useEffect(() => {
    const initializeChat = async () => {
      try {
        const token = await AsyncStorage.getItem('token');
        if (!token) {
          console.error('Token not found in AsyncStorage');
          throw new Error('Token not found');
        }
  
        console.log('Retrieved token:', token);
  
        const decoded = decodeToken(token);
        if (!decoded) {
          console.error('Decoded token is null');
          throw new Error('Failed to decode token');
        }
  
        if (!decoded._id && !decoded.sub) {
          console.error('Token is missing user ID:', decoded);
          throw new Error('Invalid token structure or missing user ID');
        }
  
        const extractedUserId = decoded._id || decoded.sub;
        console.log('Extracted User ID:', extractedUserId);
  
        setUserId(extractedUserId);
  
        // Fetch chat history
        const response = await api.get(
          `/api/messages/history?senderId=${extractedUserId}&receiverId=${friendId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        console.log('Chat history response:', response.data);
        setMessages(response.data);
  
        // Connect WebSocket here if needed (implementation removed in your case)
      } catch (error) {
        console.error('Error initializing chat:', error.message);
        Alert.alert('Error', 'Failed to initialize chat');
      }
    };
  
    initializeChat();
  
    // Cleanup function
    const localStompClient = stompClient.current; // Capture the current value
    return () => {
      if (localStompClient) {
        localStompClient.disconnect(() => {
          console.log('WebSocket disconnected');
        });
      }
    };
  }, [friendId]);
  

  const sendMessage = async () => {
    console.log("Sending message:", {
        senderId: userId,
        receiverId: friendId,
        content: messageText,
    });
    try {
        const token = await AsyncStorage.getItem('token');
        const message = {
            senderId: userId, // Updated key
            receiverId: friendId, // Updated key
            content: messageText,
        };

        await api.post('/api/messages/send', message, {
            headers: { Authorization: `Bearer ${token}` },
        });

        setMessages((prevMessages) => [
            ...prevMessages,
            { sender: userId, receiver: friendId, content: messageText, timestamp: new Date().toISOString() },
        ]);

        setMessageText('');
    } catch (error) {
        console.error('Error sending message:', error.message);
        Alert.alert('Error', 'Failed to send message');
    }
};


  return (
    <View style={styles.container}>
      <Text style={styles.header}>{friendName}</Text>
      <FlatList
        data={messages}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item }) => (
          <Text style={styles.message}>
            {item.sender === userId ? 'You' : item.sender}: {item.content}
          </Text>
        )}
      />
      <TextInput
        style={styles.input}
        placeholder="Type your message"
        value={messageText}
        onChangeText={setMessageText}
      />
      <Button title="Send" onPress={sendMessage} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 10 },
  header: { fontSize: 20, fontWeight: 'bold', marginBottom: 10 },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10 },
  message: { padding: 5, borderBottomWidth: 1, borderColor: '#eee' },
});
