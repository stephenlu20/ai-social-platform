import React, { useState } from 'react';
import { UserProvider } from './context/UserContext';
import Layout from './components/layout/Layout';
import './styles/App.css';

function App() {
  // State-based routing
  const [currentView, setCurrentView] = useState('home'); // 'home', 'profile', 'search', 'explore', etc.
  const [viewParams, setViewParams] = useState({}); // For passing data like userId to profile view

  const navigateTo = (view, params = {}) => {
    setCurrentView(view);
    setViewParams(params);
  };

  return (
    <UserProvider>
      <div className="App">
        <Layout 
          currentView={currentView}
          viewParams={viewParams}
          navigateTo={navigateTo}
        />
      </div>
    </UserProvider>
  );
}

export default App;