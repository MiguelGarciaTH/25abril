import React, { useState } from "react";
import ArticleTop from "../components/ArticleTop";
import Header from "../components/Header";
import "../styles/ArticleTop.css";

const Data = () => {
  const [size, setSize] = useState(5); // Initialize with the default size
  const [toggle, setToggle] = useState("Relevância"); // Initialize toggle state

  const handleSliderChange = (event) => {
    const newSize = parseInt(event.target.value, 10); // Ensure the value is an integer
    setSize(newSize); // Update local state
  };

  const handleToggleClick = () => {
    setToggle((prev) => (prev === "Relevância" ? "Entidades" : "Relevância")); // Toggle between the two states
  };

  return (
    <div className="dataContainer">
      <Header isHome={false} />
      <span style={{ display: "block", height: "100px" }}></span>

      {/* Flex container for slider and toggle */}
      <div className="controlsContainer" style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: "20px" }}>

        {/* Sliding Toggle Button */}
        <div className="toggleContainer">
          <div className="toggleWrapper" onClick={handleToggleClick}>
            <div className={`toggleSlider ${toggle === "Relevância" ? "left" : "right"}`}></div>
            <span className="toggleOption">Relevância</span>
            <span className="toggleOption">Entidades</span>
          </div>
        </div>
        {/* Slider input */}
        <div className="sliderContainer">
          <label htmlFor="sizeSlider">
            Top <span style={{ color: "#cc0000" }}>{size}</span> de artigos
          </label>
          <input
            id="sizeSlider"
            type="range"
            min="1"
            max="100"
            value={size}
            onChange={handleSliderChange}
          />
        </div>
      </div>
      <span style={{ display: "block", height: "50px" }}></span>

      {/* Pass size and toggle as props to ArticleTop */}
      <ArticleTop size={size} toggle={toggle} />
    </div>
  );
};

export default Data;
