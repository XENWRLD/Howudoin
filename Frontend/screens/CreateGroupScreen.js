import React, { useState } from 'react';
import { View, TextInput, Button, Alert, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';

export default function CreateGroupScreen({ navigation }) {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [memberIds, setMemberIds] = useState(''); // Comma-separated IDs

    const handleCreateGroup = async () => {
        try {
            const token = await AsyncStorage.getItem('token');
            if (!token) throw new Error('Token not found');

            const membersArray = memberIds.split(',').map((id) => id.trim());
            const payload = { name, description, memberIds: membersArray };

            await api.post('/api/groups/create', payload, {
                headers: { Authorization: `Bearer ${token}` },
            });

            Alert.alert('Success', 'Group created successfully');
            navigation.goBack();
        } catch (error) {
            console.error('Error creating group:', error.message);
            Alert.alert('Error', 'Failed to create group');
        }
    };

    return (
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                placeholder="Group Name"
                value={name}
                onChangeText={setName}
            />
            <TextInput
                style={styles.input}
                placeholder="Group Description"
                value={description}
                onChangeText={setDescription}
            />
            <TextInput
                style={styles.input}
                placeholder="Member IDs (comma-separated)"
                value={memberIds}
                onChangeText={setMemberIds}
            />
            <Button title="Create" onPress={handleCreateGroup} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10 },
    input: { borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10 },
});
