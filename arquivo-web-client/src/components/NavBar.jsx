import React from "react";
import { Link } from "react-router-dom";

const NavBar = () => {
    return (
        <nav>
            <Link to="/">Home</Link>
            <br />
            <Link to="/contact">Contacts</Link>
            <br />
            <Link to="/artistas">Artistas</Link>
            <br />
            <Link to="/politicos">Politicos</Link>
            <br />
            <Link to="/partidos">Partidos</Link>
            <br />
            <Link to="/eventos">Eventos</Link>
            <br />
            <Link to="/movimentos">Movimentos</Link>
        </nav>
    )
}

export default NavBar;