import React, { useEffect, useState } from 'react';
import { View, Text, Button, Alert } from 'react-native';
import api from '../services/api';

export default function ProtectedScreen() {
  const [data, setData] = useState('');

  const fetchData = async () => {
    try {
      const response = await api.get('/api/protected-endpoint'); // Replace with a real endpoint
      setData(response.data);
    } catch (error) {
      Alert.alert('Error', error.response?.data || 'Failed to fetch data');
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <View>
      <Text>Protected Data: {data}</Text>
    </View>
  );
}
