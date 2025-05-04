import React from "react";
import Header from "../components/Header";
import SearchEntityCounter from "../components/SearchEntityCounter";
import SiteCounter from "../components/SiteCounter";
import SearchEntityTypeCounter from "../components/SearchEntityTypeCounter";
import "../index.css";

const Stats = () => {
    const isMobile = window.innerWidth <= 768;

    return (
        <div className="data-container">
            <Header isHome={false} />
            <SearchEntityTypeCounter />
            <span style={{ display: "block", height: "90px" }}></span>
            {!isMobile && <SearchEntityCounter />}
            {!isMobile && <span style={{ display: "block", height: "90px" }}></span>}
            <SiteCounter />
        </div>
    )
}

export default Stats;