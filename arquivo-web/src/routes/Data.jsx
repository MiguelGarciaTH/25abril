import React from "react";
import Header from "../components/Header";
import "../index.css";

const Data = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <span style={{ display: "block", height: "50px" }}></span>
        </div>
    )
}

export default Data;