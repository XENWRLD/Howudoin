import React, { useState, useEffect } from 'react';
import { FlatList, TouchableOpacity, Text, Alert, View } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api'; // Adjust the path as needed

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
    const base64Url = token.split('.')[1]; // Extract the payload part of the JWT
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const decodedPayload = JSON.parse(decodeBase64(base64)); // Decode Base64 to JSON
    return decodedPayload;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};

export default function FriendPageScreen({ navigation }) { // Accept navigation prop
  const [friends, setFriends] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        setLoading(true);

        // Retrieve the token from AsyncStorage
        const token = await AsyncStorage.getItem('token');
        console.log('Token:', token);
        if (!token) throw new Error('Token not found');

        // Decode the token manually
        const decodedToken = decodeToken(token);
        console.log('Decoded Token:', decodedToken);

        // Extract the user ID (assume `_id` is the correct field)
        const userId = decodedToken?.sub || decodedToken?._id;
        if (!userId) throw new Error('User ID not found in token');
        console.log('User ID:', userId);

        // Fetch friends using the user ID
        const response = await api.get(`/api/friends/${userId}/list`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log('Friends Response:', response.data);

        // Update the state with the fetched friends
        setFriends(response.data);
      } catch (error) {
        console.error('Error fetching friends:', error);
        Alert.alert('Error', 'Failed to fetch friends');
      } finally {
        setLoading(false);
      }
    };

    fetchFriends();
  }, []);

  return (
    <FlatList
      data={friends}
      keyExtractor={(item) => item.id} // Using `id` from UserProjection
      renderItem={({ item }) => (
        <TouchableOpacity
          onPress={() =>
            navigation.navigate('FriendChatScreen', {
              friendId: item.id,
              friendName: `${item.name} ${item.lastName}`,
            })
          }
        >
          <View style={{ padding: 10, borderBottomWidth: 1, borderColor: '#ccc' }}>
            <Text style={{ fontSize: 16, fontWeight: 'bold' }}>{`${item.name} ${item.lastName}`}</Text>
            <Text style={{ fontSize: 14, color: 'gray' }}>{item.email}</Text>
          </View>
        </TouchableOpacity>
      )}
    />
  );
}
