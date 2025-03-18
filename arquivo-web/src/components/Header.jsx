// Header.js
import React from 'react';
import { Link } from 'react-router';

import "../styles/Header.css"; // Include the CSS for styling

function Header({ isHome }) {
    if (isHome) {
        return (
            <header className="header-page-home">
                <h1><Link className="header-page-link" to="/">[ARQUIVO 25 DE ABRIL]</Link></h1>
            </header>
        );
    } else {
        return (
            <header className="header-page">
                <h1><Link className="header-page-link" to="/">[ARQUIVO 25 DE ABRIL]</Link></h1>
            </header>
        );
    }
};

export default Header;
