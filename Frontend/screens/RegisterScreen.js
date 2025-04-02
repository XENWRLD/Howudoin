import React, { useState } from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import api from '../services/api';


export default function RegisterScreen({ navigation }) {
  const [name, setName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleRegister = async () => {
    try {
      const response = await api.post('/api/users/register', {
        name,
        lastName,
        email,
        password,
      });
      Alert.alert('Success', response.data, [
        { text: 'OK', onPress: () => navigation.navigate('MainPage') },
      ]);
    } catch (error) {
      Alert.alert('Error', error.response?.data || 'Registration failed');
    }
  };

  return (
    <View>
      <Text>Register</Text>
      <TextInput placeholder="Name" onChangeText={setName} value={name} />
      <TextInput placeholder="Last Name" onChangeText={setLastName} value={lastName} />
      <TextInput placeholder="Email" onChangeText={setEmail} value={email} keyboardType="email-address" />
      <TextInput placeholder="Password" onChangeText={setPassword} value={password} secureTextEntry />
      <Button title="Register" onPress={handleRegister} />
    </View>
  );
}

