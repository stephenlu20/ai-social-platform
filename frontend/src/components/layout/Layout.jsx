
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
    <div className="max-w-[1400px] mx-auto grid grid-cols-[280px_1fr_380px] gap-5 px-5
                    lg:grid-cols-[280px_1fr_380px]
                    md:grid-cols-[80px_1fr_300px]
                    sm:grid-cols-1">
      <Sidebar onNavigateToDebates={handleNavigateToDebates} />
      <MainFeed refreshTrigger={refreshTrigger} debateFilterRequest={debateFilterRequest} />
      <RightSidebar onPostCreated={handlePostCreated} />
    </div>
  );
}

export default Layout;