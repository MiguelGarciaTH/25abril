import React from "react";
import Header from "../components/Header";
import SearchEntityCounter from "../components/SearchEntityCounter";
import SiteCounter from "../components/SiteCounter";
import "../index.css";

const Stats = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <SearchEntityCounter />
            <span style={{ display: "block", height: "50px" }}></span>
            <SiteCounter />
        </div>
    )
}

export default Stats;