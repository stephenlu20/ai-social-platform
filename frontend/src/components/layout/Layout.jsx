

import React from 'react';
import Sidebar from './Sidebar';
import MainFeed from '../feed/MainFeed';
import RightSidebar from './RightSidebar';
import './Layout.css';

function Layout() {
  return (
    <div className="layout-container">
      <Sidebar />
      <MainFeed />
      <RightSidebar />
    </div>
  );
}

export default Layout;