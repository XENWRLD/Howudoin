import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TextInput, Button, StyleSheet } from 'react-native';
import api from '../services/api';

export default function GroupChatScreen({ route }) {
    const { groupId, groupName } = route.params;
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const response = await api.get(`/api/groups/${groupId}/messages`);
                setMessages(response.data);
            } catch (error) {
                console.error('Failed to fetch messages:', error.message);
            }
        };
        fetchMessages();
    }, [groupId]);

    const sendMessage = async () => {
        try {
            await api.post(`/api/groups/${groupId}/sendMessage`, { content: newMessage });
            setNewMessage(''); // Clear input
            const response = await api.get(`/api/groups/${groupId}/messages`); // Refresh messages
            setMessages(response.data);
        } catch (error) {
            console.error('Failed to send message:', error.message);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>{groupName}</Text>
            <FlatList
                data={messages}
                keyExtractor={(item, index) => index.toString()}
                renderItem={({ item }) => (
                    <Text style={styles.message}>
                        <Text style={styles.sender}>{item.sender}: </Text>
                        {item.content}
                    </Text>
                )}
            />
            <View style={styles.inputContainer}>
                <TextInput
                    style={styles.input}
                    value={newMessage}
                    onChangeText={setNewMessage}
                    placeholder="Type a message"
                />
                <Button title="Send" onPress={sendMessage} />
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10 },
    title: { fontSize: 20, fontWeight: 'bold', marginBottom: 10 },
    message: { padding: 5, borderBottomWidth: 1, borderColor: '#eee' },
    sender: { fontWeight: 'bold' },
    inputContainer: { flexDirection: 'row', alignItems: 'center', marginTop: 10 },
    input: { flex: 1, borderWidth: 1, borderColor: '#ccc', padding: 5, marginRight: 10 },
});
