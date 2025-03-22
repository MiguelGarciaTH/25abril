// Header.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router';

import "../styles/Header.css"; // Include the CSS for styling

function Header({ isHome }) {
    const [isMobile, setIsMobile] = useState(false);

    useEffect(() => {
        const handleResize = () => {
            setIsMobile(window.innerWidth <= 768); // Adjust breakpoint as needed
        };

        handleResize(); // Check on initial render
        window.addEventListener('resize', handleResize);

        return () => window.removeEventListener('resize', handleResize);
    }, []);

    if (isHome) {
        if (isMobile) {
            const fontSize = 24; // Adjust font size as needed
            return (
                <header className="header-page-home" style={{ marginTop: "50%" }}>
                    <h1>
                        <span style={{ fontSize: "120px" }}>[</span>
                        <span style={{ display: "inline-block", textAlign: "center", fontSize:"46px" }}>
                            Arquivo<br />25 de Abril
                        </span>
                        <span style={{ fontSize: "120px" }}>]</span>
                    </h1>
                </header>
            );
        } else {
            return (
                <header className="header-page-home">
                    <h1><Link className="header-page-link" to="/">[ARQUIVO 25 DE ABRIL]</Link></h1>
                </header>
            );
        }
    } else {
        return (
            <header className="header-page">
                <h1><Link className="header-page-link" to="/">[ARQUIVO 25 DE ABRIL]</Link></h1>
            </header>
        );
    }
};

export default Header;
