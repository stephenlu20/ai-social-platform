
import React from 'react';
import ComposeBox from '../feed/ComposeBox';

function RightSidebar({ onPostCreated }) {
  return (
    <div className="py-[30px]">
      {/* Compose Box */}
      <div className="sticky top-[30px]">
        <ComposeBox onPostCreated={onPostCreated} />
      </div>
    </div>
  );
}

export default RightSidebar;
