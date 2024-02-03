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
            <Link className="navbar-link" to="/entity/ARTISTA">ARTISTAS</Link>
            <br />
            <Link className="navbar-link" to="/entity/MOVIMENTO">MOVIMENTOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/POLITICO">POLITICOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/PARTIDO">PARTIDOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/EVENTO">EVENTOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/JORNAL">JORNAIS</Link>
            <br />
            <Link className="navbar-link" to="/entity/PRISAO">PRISÕES</Link>
        </nav>
    )
}

export default NavBar;