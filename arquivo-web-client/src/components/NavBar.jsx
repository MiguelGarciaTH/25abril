import React from "react";
import { Link } from "react-router-dom";

const NavBar = () => {
    
    return (
        
        <nav className="navbar">
            <Link class="navbar-link" to="/"> <span class="title-header1">25</span><span class="title-header2">ABRIL</span></Link>
            <br />
            <br />
            <br />
            <Link className="navbar-link" to="/">INICIO</Link>
            <br />
            <Link className="navbar-link" to="/entity/ARTISTAS">ARTISTAS</Link>
            <br />
            <Link className="navbar-link" to="/entity/MOVIMENTOS">MOVIMENTOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/POLITICOS">POLITICOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/PARTIDOS">PARTIDOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/EVENTOS">EVENTOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/LOCAIS">LOCAIS</Link>
            <br />
            <Link className="navbar-link" to="/about">SOBRE</Link>
            <br />
            <Link className="navbar-link" to="/architecture">ARQUITECTURA</Link>
        </nav>
    )
}

export default NavBar;