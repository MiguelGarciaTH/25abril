import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { HiHome } from 'react-icons/hi';
import { BsPeople } from 'react-icons/bs';
import { AiOutlineBarChart } from "react-icons/ai";
import { AiFillFileText } from "react-icons/ai";
import { AiOutlineGithub } from "react-icons/ai";
import { AiOutlineDeploymentUnit } from "react-icons/ai";
import "../styles/SideBar.css";

const Sidebar = () => {
    const [isVisible, setIsVisible] = useState(false);

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
                            <span className="link-text">Entidades</span>
                        </Link>
                        <Link to="/stats" className="icon-link">
                            <AiOutlineBarChart className="sidebar-icon" />
                            <span className="link-text">Estatisticas</span>
                        </Link>
                        <Link to="/data" className="icon-link">
                            <AiOutlineDeploymentUnit className="sidebar-icon" />
                            <span className="link-text">Dados</span>
                        </Link>
                        <Link to="/about" className="icon-link">
                            <AiFillFileText className="sidebar-icon" />
                            <span className="link-text">Sobre</span>
                        </Link>
                        <Link to="https://github.com/MiguelGarciaTH/25abril" className="icon-link">
                            <AiOutlineGithub className="sidebar-icon" />
                            <span className="link-text">github</span>
                        </Link>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Sidebar;