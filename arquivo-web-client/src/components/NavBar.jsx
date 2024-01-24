import React from "react";
import { Link } from "react-router-dom";

const NavBar = () => {
    
    return (
        <nav className="navbar">
            <Link className="navbar-link" to="/">Home</Link>
            <br />
            <Link className="navbar-link" to="/contact">Contacts</Link>
            <br />
            <Link className="navbar-link" to="/entity/ARTISTA">Artistas</Link>
            <br />
            <Link className="navbar-link" to="/entity/MOVIMENTO">Movimentos</Link>
            <br />
            <Link className="navbar-link" to="/entity/POLITICO">Politicos</Link>
            <br />
            <Link className="navbar-link" to="/entity/PARTIDO">Partidos</Link>
            <br />
            <Link className="navbar-link" to="/entity/EVENTO">Eventos</Link>
            <br />
            <Link className="navbar-link" to="/entity/JORNAL">Jornais</Link>
            <br />
            <Link className="navbar-link" to="/entity/PRISAO">Prisões</Link>
        </nav>
    )
}

export default NavBar;