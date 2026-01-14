
import React, { createContext, useState, useContext, useEffect } from 'react';
import userService from '../services/userService';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAllUsers();
  }, []);

  const loadAllUsers = async () => {
    try {
      const users = await userService.getAllUsers();
      setAllUsers(users);
      
      const savedUserId = localStorage.getItem('selectedUserId');
      if (savedUserId && users.length > 0) {
        const savedUser = users.find(u => u.id === savedUserId);
        if (savedUser) {
          setCurrentUser(savedUser);
        } else {
          setCurrentUser(users[0]);
        }
      } else if (users.length > 0) {
        setCurrentUser(users[0]);
      }
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      setLoading(false);
    }
  };

  const switchUser = (userId) => {
    const user = allUsers.find(u => u.id === userId);
    if (user) {
      setCurrentUser(user);
      localStorage.setItem('selectedUserId', userId);
    }
  };

  return (
    <UserContext.Provider value={{ currentUser, allUsers, loading, switchUser }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useUser must be used within UserProvider');
  }
  return context;
};
