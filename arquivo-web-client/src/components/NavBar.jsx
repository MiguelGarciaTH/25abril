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
            <Link className="navbar-link" to="/entity/CAPITAES">CAPITÃES</Link>
            <br />
            <Link className="navbar-link" to="/entity/RESISTENTES">RESISTENTES</Link>
            <br />
            <Link className="navbar-link" to="/entity/CANTORES">CANTORES</Link>
            <br />
            <Link className="navbar-link" to="/entity/ESCRITORES">ESCRITORES</Link>
            <br />
            <Link className="navbar-link" to="/entity/JORNALISTAS">MENSAGEIROS</Link>
            <br />
            <Link className="navbar-link" to="/entity/OPRESSORES">OPRESSORES</Link>
            <br />
            <Link className="navbar-link" to="/entity/MOVIMENTOS">MOVIMENTOS</Link>
            <br />
            <Link className="navbar-link" to="/entity/EVENTOS">EVENTOS</Link>
            <br />
            <Link className="navbar-link" to="/about">SOBRE</Link>
            <br />
            <Link className="navbar-link" to="/architecture">DETALHES</Link>
        </nav>
    )
}

export default NavBar;