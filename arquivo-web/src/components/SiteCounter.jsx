import React, { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { Link } from "react-router-dom";
import "../styles/ArticleTop.css";

// Custom tooltip component
const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const { id, name, count } = payload[0].payload;  // Get the data from the tooltip's payload

    return (
      <div style={{
        pointerEvents: "auto", // Ensure the tooltip allows pointer events
        backgroundColor: "#fff",
        border: "1px solid #ccc",
        padding: "10px",
        borderRadius: "4px",
        boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        maxWidth: "200px",
        minWidth: "200px",
        textAlign: "center", // Center-align content, including the image
      }}
      >
        <Link to={`/articles/${id}/${name}/site`} className="customToolTipLink">
          <p style={{ fontWeight: 'bold' }}>{name}</p>
          <p>Artigos: {count}</p>
        </Link>
      </div>
    );
  }
  return null;
};

const SiteCounter = () => {
  const [data, setData] = useState([]);

  // Fetch data from the API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_REST_URL}/site/stats`);
        const result = await response.json();
        setData(result);  // Assuming result is an array of objects
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  return (

    <div style={{ width: "90%", height: 400 }}>
      <h3 style={{ textAlign: 'center', marginBottom: '20px' }}>Artigos por site</h3> {/* Add title here */}
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="name"
            angle={0} // Keep the labels horizontal
            textAnchor="middle"  // Center the text horizontally
            tick={{
              fontSize: 16,
              textAnchor: 'middle',
              dominantBaseline: 'middle'
            }} // Adjust label positioning
            interval={0} // Ensure all labels are rendered
            height={100} // Set height to accommodate wrapped text
            style={{
              wordWrap: 'break-word',
              whiteSpace: 'normal',
              overflow: 'hidden',
              maxWidth: '100px' // Add maximum width for wrapping
            }}
          />
          <YAxis />
          <Tooltip content={<CustomTooltip />} />
          <Legend />
          <Bar dataKey="count" fill="#333" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default SiteCounter;
