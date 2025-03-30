import React from "react";
import { Link } from "react-router-dom";
import Header from "../components/Header";
import "../index.css";

const Architecture = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <div class="text">
                <h2>Motivação</h2>
                <img src="arquitectura.png" alt="Architecture" className="architecture-image" />
            </div>
        </div>
    )
}

export default Architecture;