import React, { useState, useEffect } from 'react';
import { View, TextInput, Button, FlatList, Text, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api'; // Adjust path as needed

// Helper functions for decoding JWT tokens
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

const decodeToken = (token) => {
  try {
    const base64Url = token.split('.')[1]; // Extract the payload part of the JWT
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const decodedPayload = JSON.parse(decodeBase64(base64)); // Decode Base64 to JSON
    return decodedPayload;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};

const FriendRequestScreen = () => {
  const [friendSearch, setFriendSearch] = useState('');
  const [pendingRequests, setPendingRequests] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch pending requests on mount
  useEffect(() => {
    const fetchPendingRequests = async () => {
      try {
        setLoading(true);
        const token = await AsyncStorage.getItem('token');
        const userId = await getUserIdFromToken(token); // Use the same token decoding logic
        const response = await api.get(`/api/friends/${userId}/pendingRequests`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPendingRequests(response.data);
      } catch (error) {
        console.error('Error fetching pending requests:', error);
        Alert.alert('Error', 'Failed to fetch pending requests');
      } finally {
        setLoading(false);
      }
    };

    fetchPendingRequests();
  }, []);

  // Extract user ID from token
  const getUserIdFromToken = async (token) => {
    const decodedToken = decodeToken(token);
    return decodedToken?._id || decodedToken?.sub;
  };

  // Handle sending friend requests
  const handleSendRequest = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const userId = await getUserIdFromToken(token);

      await api.post(`/api/friends/${userId}/sendRequest`, null, {
        params: { friendId: friendSearch },
        headers: { Authorization: `Bearer ${token}` },
      });

      Alert.alert('Success', 'Friend request sent successfully');
      setFriendSearch(''); // Clear the search field
    } catch (error) {
      console.error('Error sending friend request:', error);
      Alert.alert('Error', 'Failed to send friend request');
    }
  };

  // Handle accepting a friend request
  const handleAcceptRequest = async (friendId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      const userId = await getUserIdFromToken(token);

      await api.post(`/api/friends/${userId}/acceptRequest`, null, {
        params: { friendId },
        headers: { Authorization: `Bearer ${token}` },
      });

      Alert.alert('Success', 'Friend request accepted');
      setPendingRequests((prevRequests) =>
        prevRequests.filter((request) => request !== friendId)
      );
    } catch (error) {
      console.error('Error accepting friend request:', error);
      Alert.alert('Error', 'Failed to accept friend request');
    }
  };

  const handleRejectRequest = async (friendId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      const userId = await getUserIdFromToken(token); // Extract the userId from the token

      // Send a POST request to reject the friend request
      await api.post(`/api/friends/${userId}/rejectRequest`, null, {
        params: { friendId },
        headers: { Authorization: `Bearer ${token}` },
      });

      Alert.alert('Success', 'Friend request rejected');
      setPendingRequests((prevRequests) =>
        prevRequests.filter((request) => request !== friendId)
      );
    } catch (error) {
      console.error('Error rejecting friend request:', error);
      Alert.alert('Error', 'Failed to reject friend request');
    }
  };



  return (
    <View style={{ padding: 20 }}>
      <TextInput
        placeholder="Search for a friend by ID"
        value={friendSearch}
        onChangeText={setFriendSearch}
        style={{
          borderWidth: 1,
          borderColor: '#ccc',
          padding: 10,
          marginBottom: 10,
          borderRadius: 5,
        }}
      />
      <Button title="Send Friend Request" onPress={handleSendRequest} disabled={!friendSearch} />

      <Text style={{ fontSize: 18, fontWeight: 'bold', marginVertical: 20 }}>Pending Requests</Text>
      <FlatList
        data={pendingRequests}
        keyExtractor={(item) => item}
        renderItem={({ item }) => (
          <View
            style={{
              flexDirection: 'row',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginBottom: 10,
              padding: 10,
              borderWidth: 1,
              borderColor: '#ccc',
              borderRadius: 5,
            }}
          >
            <Text>{item}</Text>
            <Button title="Accept" onPress={() => handleAcceptRequest(item)} />
            <Button title="Reject" onPress={() => handleRejectRequest(item)} color="red" />
          </View>
        )}
      />
    </View>
  );
};

export default FriendRequestScreen;