import React from 'react';
import { NavigationContainer, RouteProp } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import RegisterScreen from './screens/RegisterScreen';
import LoginScreen from './screens/LoginScreen';
import MainPageScreen from './screens/MainPageScreen';
import FriendPageScreen from './screens/FriendPageScreen';
import GroupPageScreen from './screens/GroupPageScreen';
import GroupChatScreen from './screens/GroupChatScreen'; // Import GroupChatScreen
import GroupDetailScreen from './screens/GroupDetailScreen'; // Import GroupDetailScreen
import CreateGroupScreen from './screens/CreateGroupScreen'; // Import CreateGroupScreen
import FriendRequestScreen from './screens/FriendRequestScreen';
import FriendChatScreen from './screens/FriendChatScreen';
import GroupRequestScreen from './screens/GroupRequestScreen'; // Import GroupRequestScreen
import { View, Text, Button, StyleSheet } from 'react-native';

// Define the stack navigator
const Stack = createStackNavigator<RootStackParamList>();

// Define type for route parameters
type RootStackParamList = {
  Home: undefined;
  LoginScreen: undefined;
  RegisterScreen: undefined;
  MainPage: undefined;
  FriendPageScreen: undefined;
  FriendChatScreen: { friendName: string; friendId: string };
  GroupPageScreen: undefined;
  GroupChatScreen: { groupId: string; groupName: string };
  GroupDetailScreen: { group: { name: string; description: string; memberIds: string[]; createdAt?: string } };
  CreateGroupScreen: undefined;
  FriendRequestScreen: undefined;
  GroupRequestScreen: undefined;
};

function HomeScreen({ navigation }: { navigation: any }) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome</Text>
      <Button title="Login" onPress={() => navigation.navigate('LoginScreen')} />
      <Button title="Register" onPress={() => navigation.navigate('RegisterScreen')} />
    </View>
  );
}

export default function App() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="LoginScreen" component={LoginScreen} />
          <Stack.Screen name="RegisterScreen" component={RegisterScreen} />
          <Stack.Screen name="MainPage" component={MainPageScreen} />
          <Stack.Screen name="FriendPageScreen" component={FriendPageScreen} />
          <Stack.Screen
            name="FriendChatScreen"
            component={FriendChatScreen}
            options={({ route }: { route: RouteProp<RootStackParamList, 'FriendChatScreen'> }) => ({
              title: route.params.friendName || 'Chat',
            })}
          />
          <Stack.Screen name="GroupPageScreen" component={GroupPageScreen} />
          <Stack.Screen
            name="GroupChatScreen"
            component={GroupChatScreen}
            options={({ route }: { route: RouteProp<RootStackParamList, 'GroupChatScreen'> }) => ({
              title: route.params.groupName || 'Group Chat',
            })}
          />
          <Stack.Screen
            name="GroupDetailScreen"
            component={GroupDetailScreen}
            options={({ route }: { route: RouteProp<RootStackParamList, 'GroupDetailScreen'> }) => ({
              title: route.params?.group?.name || 'Group Details',
            })}
          />
          <Stack.Screen name="CreateGroupScreen" component={CreateGroupScreen} />
          <Stack.Screen name="FriendRequestScreen" component={FriendRequestScreen} />
          <Stack.Screen name="GroupRequestScreen" component={GroupRequestScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
});
