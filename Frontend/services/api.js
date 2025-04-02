import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Create an Axios instance
const api = axios.create({
  baseURL: 'http://10.0.2.2:8080', // Use appropriate URL for Android emulator
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' },
});

// Interceptor to add Authorization header
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;

export const getGroups = async (token) => {
  return await api.get('/api/groups/user/groups', {
      headers: { Authorization: `Bearer ${token}` },
  });
};

export const createGroup = async (token, groupData) => {
  return await api.post('/api/groups/create', groupData, {
      headers: { Authorization: `Bearer ${token}` },
  });
};
