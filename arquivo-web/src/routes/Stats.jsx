import React from "react";
import Header from "../components/Header";
import SearchEntityCounter from "../components/SearchEntityCounter";
import SiteCounter from "../components/SiteCounter";
import SearchEntityTypeCounter from "../components/SearchEntityTypeCounter";
import "../index.css";

const Stats = () => {
    return (
        <div className="data-container">
            <Header isHome={false} />
            <SearchEntityTypeCounter />
            <span style={{ display: "block", height: "90px" }}></span>
            <SearchEntityCounter />
            <span style={{ display: "block", height: "90px" }}></span>
            <SiteCounter />
        </div>
    )
}

export default Stats;