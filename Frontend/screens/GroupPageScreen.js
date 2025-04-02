import React, { useState, useEffect } from 'react';
import { FlatList, TouchableOpacity, Text, Alert, Button, View, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';
import { useNavigation } from '@react-navigation/native';

// Groups Page Screen
export default function GroupPageScreen() {
  const [groups, setGroups] = useState([]);
  const navigation = useNavigation(); // For navigation

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const token = await AsyncStorage.getItem('token');
        const response = await api.get('/api/groups/user/groups', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setGroups(response.data);
      } catch (error) {
        console.error('Error fetching groups:', error.message);
        Alert.alert('Error', 'Failed to fetch groups');
      }
    };
    fetchGroups();
  }, []);

  const navigateToGroupDetails = (group) => {
    if (!group.id) {
        console.error('Group ID is missing:', group);
        Alert.alert('Error', 'Invalid group ID. Please try again.');
        return;
    }
    navigation.navigate('GroupDetailScreen', { groupId: group.id });
};

const navigateToGroupChat = (group) => {
  if (!group.id) {
      console.error('Group ID is missing:', group);
      Alert.alert('Error', 'Invalid group ID. Please try again.');
      return;
  }
  console.log('Navigating to group chat with group:', group);
  navigation.navigate('GroupChatScreen', { groupId: group.id, groupName: group.name });
};


  // Navigate to Create Group Screen
  const navigateToCreateGroup = () => {
    navigation.navigate('CreateGroupScreen'); // Ensure the screen name matches App.tsx
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={groups}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View>
            <TouchableOpacity onPress={() => navigateToGroupDetails(item)}>
              <Text style={styles.groupItem}>Details: {item.name}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => navigateToGroupChat(item)}>
              <Text style={styles.groupItem}>Chat: {item.name}</Text>
            </TouchableOpacity>
          </View>
        )}
      />
      <Button title="Create Group" onPress={navigateToCreateGroup} />
    </View>
  );
  
}



const styles = StyleSheet.create({
  container: { flex: 1, padding: 10 },
  groupItem: { padding: 10, borderBottomWidth: 1, borderColor: '#ccc' },
});
