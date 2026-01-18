import React, { useState } from 'react';
import Sidebar from './Sidebar';
import MainFeed from '../feed/MainFeed';
import RightSidebar from './RightSidebar';

function Layout() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [debateFilterRequest, setDebateFilterRequest] = useState(null);

  const handlePostCreated = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  const handleNavigateToDebates = (filter) => {
    setDebateFilterRequest({ filter, timestamp: Date.now() });
  };

  return (
    <div className="min-h-screen flex justify-center px-5">
      <div className="w-full max-w-[1400px] grid gap-5 py-5
                      grid-cols-1
                      sm:grid-cols-1
                      md:grid-cols-[80px_1fr_300px]
                      lg:grid-cols-[280px_1fr_380px]">
        <Sidebar onNavigateToDebates={handleNavigateToDebates} />
        <MainFeed refreshTrigger={refreshTrigger} debateFilterRequest={debateFilterRequest} />
        <RightSidebar onPostCreated={handlePostCreated} />
      </div>
    </div>
  );
}

export default Layout;