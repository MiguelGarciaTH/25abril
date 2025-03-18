import React from "react";
import Header from "../components/Header";
import ParentComponent from "../components/ParentComponent";
import '../index.css';

const Home = () => {

    return (
        <div className="container">
            <div className="div1"><Header isHome={true} /></div>
            <div className="div2"><ParentComponent /></div>
        </div>
    )
}

export default Home;
