import React from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/Header';
import '../index.css';

const Error404 = () => {
    return (
        <div className="container">
            <div className="div1"><Header isHome={true} /></div>
            <div className="div2" style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100%',
                textAlign: 'center',
                marginTop: '-20%',
                fontFamily: 'Mona Sans Condensed, sans-serif'

            }}>
                <img
                    src="/android-chrome-512x512.png"
                    alt="Search"
                    style={{ width: '200px', height: '200px' }}
                />
                <h1>404 - Page Not Found</h1>

                <Link to="/home"><h2>Página inicial</h2></Link>
            </div>
        </div>
    );
};

export default Error404;
