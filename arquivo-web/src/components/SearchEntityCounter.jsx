import React, { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { Link } from "react-router-dom";
import "../styles/ArticleTop.css";

// Custom Tooltip component
const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const { id, name, count, image } = payload[0].payload; // Extract data from the payload
    return (
      <div
        style={{
          pointerEvents: "auto", // Ensure the tooltip allows pointer events
          backgroundColor: "#fff",
          border: "1px solid #ccc",
          padding: "5px",
          borderRadius: "4px",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
          maxWidth: "200px",
          minWidth: "200px",
          textAlign: "center", // Center-align content, including the image
        }}
      >
        <Link to={`/articles/${id}/${name}/entity`} className="customToolTipLink">
          <p style={{ fontWeight: "bold", margin: 0 }}>{name}</p>
          {image ? (
            <img
              src={image}
              alt={name}
              style={{
                width: "100%", // Make image fill the width of the container
                height: "150px", // Set a fixed height for the image
                objectFit: "contain", // Ensures the image is contained in the area
                filter: "grayscale(100%)", // Apply grayscale filter
                borderRadius: "5px",
                marginBottom: "10px",
              }}
            />
          ) : (
            <p>No image available</p> // Fallback if no image is available
          )}
          <p>Artigos: {count}</p>
        </Link>
      </div>
    );
  }
  return null;
};

const SearchEntityCounter = () => {
  const [data, setData] = useState([]);
  const [isMobile, setIsMobile] = useState(false);

  // Detect screen size for mobile responsiveness
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
    };
    handleResize(); // Initial check
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Fetch data from the API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity/stats`);
        const result = await response.json();
        setData(result); // Assuming result is an array of objects
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  return (
    <div
      style={{
        width: "90%",
        height: "90%",
        margin: "0 auto",
        padding: "10px",
      }}
    >
      <h3
        style={{
          textAlign: "center",
          marginBottom: "20px",
          fontSize: "1.5rem",
        }}
      >
        Artigos por entidade
      </h3>
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="name"
            angle={isMobile ? 45 : 0} // Adjust angle for mobile
            textAnchor={isMobile ? "left" : "middle"} // Adjust text alignment for angled labels
            tick={{
              fontSize: 16,
              textAlign: isMobile ? "left" : "center",
              textAnchor: isMobile ? "end" : "middle",
            }}
            dy={isMobile ? 120 : 10} // Add vertical offset to position labels below the axis
            dx={isMobile ? 115 : 10} // Add vertical offset to position labels below the axis
            interval={0}
            height={isMobile ? 140 : 100} // Adjust height for angled labels
            style={{
              wordWrap: "break-word",
              whiteSpace: "normal",
              overflow: "hidden",
              maxWidth: "100px",
            }}
          />
          <YAxis />
          <Tooltip
            content={<CustomTooltip />}
            wrapperStyle={{ pointerEvents: "none" }} // Prevent Tooltip from blocking interactions
          />
          <Legend />
          <Bar dataKey="count" fill="#333" />
        </BarChart>
      </ResponsiveContainer>
      <style>
        {`
          @media (max-width: 768px) {
            div {
              width: 100%;
              height: auto;
            }
            h3 {
              font-size: 1.2rem;
            }
            .recharts-wrapper {
              width: 100% !important;
              height: 300px !important;
            }
          }
        `}
      </style>
    </div>
  );
};

export default SearchEntityCounter;
