import React, { useState, useEffect } from 'react';
import { View, FlatList, Text, Button, Alert, TextInput } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';
import { Picker } from '@react-native-picker/picker';

// Helper functions for decoding JWT tokens (reuse from FriendRequestScreen)
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

const GroupRequestScreen = () => {
  const [pendingGroupRequests, setPendingGroupRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [groups, setGroups] = useState([]); // List of groups
  const [selectedGroup, setSelectedGroup] = useState(''); // Selected group ID
  const [userToInvite, setUserToInvite] = useState(''); // User ID to invite

  // Fetch pending group requests and admin groups on mount
  useEffect(() => {
    const fetchGroupRequests = async () => {
      try {
        setLoading(true);
        const token = await AsyncStorage.getItem('token');
        const userId = await getUserIdFromToken(token);

        // Fetch pending group requests
        const response = await api.get(`/api/groups/user/${userId}/pendingInvitations`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPendingGroupRequests(response.data);

        // Fetch groups where the user is an admin
        const groupResponse = await api.get('/api/groups/user/groups', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setGroups(groupResponse.data);
      } catch (error) {
        console.error('Error fetching group requests or groups:', error);
        Alert.alert('Error', 'Failed to fetch group requests or groups');
      } finally {
        setLoading(false);
      }
    };

    fetchGroupRequests();
  }, []);

  // Extract user ID from token
  const getUserIdFromToken = async (token) => {
    const decodedToken = decodeToken(token);
    return decodedToken?._id || decodedToken?.sub;
  };

  // Handle accepting a group request
  const handleAcceptRequest = async (groupId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      await api.post(`/api/groups/${groupId}/acceptInvitation`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert('Success', 'Group request accepted');
      setPendingGroupRequests((prevRequests) =>
        prevRequests.filter((request) => request.id !== groupId)
      );
    } catch (error) {
      console.error('Error accepting group request:', error);
      Alert.alert('Error', 'Failed to accept group request');
    }
  };

  // Handle rejecting a group request
  const handleRejectRequest = async (groupId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      await api.post(`/api/groups/${groupId}/rejectInvitation`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert('Success', 'Group request rejected');
      setPendingGroupRequests((prevRequests) =>
        prevRequests.filter((request) => request.id !== groupId)
      );
    } catch (error) {
      console.error('Error rejecting group request:', error);
      Alert.alert('Error', 'Failed to reject group request');
    }
  };

  // Handle sending a group invitation
  const handleSendInvitation = async () => {
    if (!selectedGroup || !userToInvite) {
      Alert.alert('Error', 'Please select a group and enter a user ID to invite.');
      return;
    }

    try {
      const token = await AsyncStorage.getItem('token');
      await api.post(`/api/groups/${selectedGroup}/sendInvitation`, null, {
        params: { userId: userToInvite },
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert('Success', 'Group invitation sent successfully');
      setUserToInvite(''); // Clear the user ID input
    } catch (error) {
      console.error('Error sending group invitation:', error);
      Alert.alert('Error', 'Failed to send group invitation');
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text style={{ fontSize: 18, fontWeight: 'bold', marginVertical: 20 }}>Pending Group Invitations</Text>
      {loading ? (
        <Text>Loading...</Text>
      ) : (
        <FlatList
          data={pendingGroupRequests}
          keyExtractor={(item) => item.id}
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
              <Text>{item.name}</Text>
              <Button title="Accept" onPress={() => handleAcceptRequest(item.id)} />
              <Button title="Reject" onPress={() => handleRejectRequest(item.id)} color="red" />
            </View>
          )}
        />
      )}

      <Text style={{ fontSize: 18, fontWeight: 'bold', marginVertical: 20 }}>Send Group Invitation</Text>
      <Picker
        selectedValue={selectedGroup}
        onValueChange={(itemValue) => setSelectedGroup(itemValue)}
        style={{
          borderWidth: 1,
          borderColor: '#ccc',
          marginBottom: 10,
          borderRadius: 5,
        }}
      >
        <Picker.Item label="Select a Group" value="" />
        {groups.map((group) => (
          <Picker.Item key={group.id} label={group.name} value={group.id} />
        ))}
      </Picker>
      <TextInput
        placeholder="Enter User ID"
        value={userToInvite}
        onChangeText={setUserToInvite}
        style={{
          borderWidth: 1,
          borderColor: '#ccc',
          padding: 10,
          marginBottom: 10,
          borderRadius: 5,
        }}
      />
      <Button title="Send Invitation" onPress={handleSendInvitation} />
    </View>
  );
};

export default GroupRequestScreen;
