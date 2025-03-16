import React, { useState } from "react";
import ArticleTop from "../components/ArticleTop";
import Header from "../components/Header";
import "../ArticleTop.css";

const Data = () => {
  const [size, setSize] = useState(5); // Initialize with the default size

  const handleSliderChange = (event) => {
    const newSize = parseInt(event.target.value, 10); // Ensure the value is an integer
    setSize(newSize); // Update local state
  };

  return (
    <div className="dataContainer">
      <Header isHome={false} />
      <span style={{ display: "block", height: "100px" }}></span>

      {/* Slider input */}
      <div className="sliderContainer">
        <label htmlFor="sizeSlider">Top {size} de artigos</label>
        <input
          id="sizeSlider"
          type="range"
          min="1"
          max="100"
          value={size}
          onChange={handleSliderChange}
        />
      </div>
      <span style={{ display: "block", height: "50px" }}></span>

      {/* Pass size as a prop to ArticleTop */}
      <ArticleTop size={size} />
    </div>
  );
};

export default Data;
