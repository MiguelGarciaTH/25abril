import React from "react";
import { Link } from "react-router-dom";

const NavBar = () => {
    
    return (
        
        <nav className="navbar">
            <div class="centered">
                <Link class="navbar-link" to="/"> <div class="title-header1">25</div><div class="title-header2">ABRIL</div></Link>
            </div>
            <br />
            <br />
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