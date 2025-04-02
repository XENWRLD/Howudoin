import React from 'react';
import { View, Text, Button } from 'react-native';

// Main Page Screen
export default function MainPageScreen({ navigation }) {
  return (
    <View style={{ padding: 20 }}>
      <Text style={{ fontSize: 20, marginBottom: 20 }}>Welcome to Howudoin</Text>
      <Button title="Friends" onPress={() => navigation.navigate('FriendPageScreen')} />
      <Button title="Groups" onPress={() => navigation.navigate('GroupPageScreen')} />
      <Button title="Friend Requests" onPress={() => navigation.navigate('FriendRequestScreen')} />
      <Button title="Group Requests" onPress={() => navigation.navigate('GroupRequestScreen')} />
    </View>
  );
}
