import React, { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { Link } from 'react-router-dom';

// Custom tooltip component
// Custom Tooltip component
const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      const { name, count, image } = payload[0].payload;  // Get the data from the tooltip's payload
      return (
        <div style={{
          backgroundColor: '#fff',
          border: '1px solid #ccc',
          padding: '10px',
          borderRadius: '4px',
          boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
          maxWidth: '300px',
        }}>
          <p style={{ fontWeight: 'bold' }}>Nome: {name}</p>
          {image ? (
          <img
            src={image}
            alt={name}
            style={{
              width: '100%',              // Make image fill the width of the container
              height: '150px',            // Set a fixed height for the image
              objectFit: 'contain',       // Ensures the image is contained in the area
              filter: 'grayscale(100%)',  // Apply grayscale filter
              borderRadius: '5px',
              marginBottom: '10px',
            }}
          />
        ) : (
          <p>No image available</p>  // Fallback if no image is available
        )}
          <p>Artigos: {count}</p>
        </div>
      );
    }
    return null;
  };

const SearchEntityCounter = () => {
  const [data, setData] = useState([]);

  // Fetch data from the API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity/stats`);
        const result = await response.json();
        console.log(result); // Log the response data to verify the structure
        setData(result);  // Assuming result is an array of objects
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  return (

    <div style={{ width: "90%", height: 400 }}>
        <h3 style={{ textAlign: 'center', marginBottom: '20px' }}>Artigos por entidade</h3> {/* Add title here */}
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
          <Tooltip content={<CustomTooltip />}/>
          <Legend />
          <Bar dataKey="count" fill="#333" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default SearchEntityCounter;
