import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import api from '../services/api';

export default function GroupDetailsScreen({ route }) {
    const { groupId } = route.params || {};
    const [groupDetails, setGroupDetails] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchGroupDetails = async () => {
            try {
                const response = await api.get(`/api/groups/${groupId}`);
                console.log('Fetched Group Details:', response.data);
                setGroupDetails(response.data);
            } catch (error) {
                console.error('Failed to fetch group details:', error.message);
                setError('Failed to fetch group details.');
            }
        };
        fetchGroupDetails();
    }, [groupId]);

    if (error) {
        return (
            <View style={styles.container}>
                <Text>Error: {error}</Text>
            </View>
        );
    }

    if (!groupDetails) {
        return (
            <View style={styles.container}>
                <Text>Loading group details...</Text>
            </View>
        );
    }

    return (
        <View style={styles.container}>
            <Text style={styles.title}>{groupDetails.name}</Text>
            <Text>Description: {groupDetails.description}</Text>
            <Text>Created At: {new Date(groupDetails.createdAt).toLocaleString()}</Text>
            <Text>Members:</Text>
            {groupDetails.memberNames.length > 0 ? (
                <FlatList
                    data={groupDetails.memberNames}
                    keyExtractor={(item, index) => index.toString()}
                    renderItem={({ item }) => <Text style={styles.member}>{item}</Text>}
                />
            ) : (
                <Text>No members found.</Text>
            )}
        </View>
    );

    /*
    return (
        <View style={styles.container}>
            <Text style={styles.title}>{groupDetails.name}</Text>
            <Text>Description: {groupDetails.description}</Text>
            <Text>Created At: {new Date(groupDetails.createdAt).toLocaleString()}</Text>
            <Text>Members:</Text>
            <FlatList
                data={groupDetails.memberNames} // it was memberIds
                keyExtractor={(item, index) => index.toString()}
                renderItem={({ item }) => <Text style={styles.member}>{item}</Text>}
            />
        </View>
    );
    */
}

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10, backgroundColor: "#fff" }, // Add backgroundColor to ensure visibility
    title: { fontSize: 20, fontWeight: "bold", marginBottom: 10 },
    member: { padding: 10, fontSize: 16, borderBottomWidth: 1, borderColor: "#ccc" }, // Adjust padding and font size
});

