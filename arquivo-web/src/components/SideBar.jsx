import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { HiHome } from 'react-icons/hi';
import { BsPeople } from 'react-icons/bs';
import { AiOutlineInfoCircle } from 'react-icons/ai';
import '../SideBar.css';

const Sidebar = () => {
    const [isVisible, setIsVisible] = useState(true);

    const toggleSidebar = () => {
        setIsVisible(!isVisible);
    };

    return (
        <>
            <div className={`hamburger-menu ${isVisible ? 'open' : ''}`} onClick={toggleSidebar}>
                <div className="bar"></div>
                <div className="bar"></div>
                <div className="bar"></div>
            </div>
            
            <div className={`sidebar ${isVisible ? 'show' : 'hide'}`}>
                <div className="sidebar-content">
                    <div className="icon-links">
                        <Link to="/home" className="icon-link">
                            <HiHome className="sidebar-icon" />
                            <span className="link-text">Home</span>
                        </Link>
                        <Link to="/entities" className="icon-link">
                            <BsPeople className="sidebar-icon" />
                            <span className="link-text">People</span>
                        </Link>
                        <Link to="/about" className="icon-link">
                            <AiOutlineInfoCircle className="sidebar-icon" />
                            <span className="link-text">About</span>
                        </Link>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Sidebar;