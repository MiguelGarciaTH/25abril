import React from "react";
import Header from "../components/Header";
import SearchEntityCounter from "../components/SearchEntityCounter";
import "../index.css";
import SiteCounter from "../components/SiteCounter";

const Data = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <SearchEntityCounter />
            <span style={{ display: "block", height: "50px" }}></span>
            <SiteCounter />
        </div>
    )
}

export default Data;