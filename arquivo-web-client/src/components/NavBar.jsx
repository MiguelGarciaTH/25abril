import React from "react";
import { Link } from "react-router-dom";

const NavBar = () => {
    
    return (
        
        <nav className="navbar">
            <Link className="navbar-link" to="/"> <span className="title-header1">25</span><span className="title-header2">ABRIL</span></Link>
            <br />
            <Link className="navbar-link" to="/">INICIO</Link>
            <br />
            <Link className="navbar-link" to="/about">SOBRE</Link>
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
            <Link className="navbar-link" to="/entity/JORNAIS">JORNAIS</Link>
            <br />
            <Link className="navbar-link" to="/entity/LOCAIS">LOCAIS</Link>
        </nav>
    )
}

export default NavBar;