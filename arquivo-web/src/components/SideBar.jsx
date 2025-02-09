import React, { useState } from "react";

import { Link } from "react-router";

import "../SideBar.css"; // Optional for custom styling


const Sidebar = () => {
  const [isVisible, setIsVisible] = useState(false);

  const toggleSidebar = () => {
    setIsVisible(!isVisible);
    //document.body.style.marginLeft = isVisible ? '0' : '300px';
  };

  return (
    <div>
      {/* Hamburger Icon */}
      <div className={`hamburger-menu ${isVisible ? "open" : ""}`} onClick={toggleSidebar}>
        <div className="bar"></div>
        <div className="bar"></div>
        <div className="bar"></div>
      </div>

      {/* Sidebar */}
      <div className={`sidebar ${isVisible ? "show" : "hide"}`}>
        <ul>
        <li><Link className="sidebar-link" to="/">Home</Link></li>
        <li><Link className="sidebar-link" to="/about">About</Link></li>
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;
