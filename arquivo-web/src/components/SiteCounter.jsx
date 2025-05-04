import React, { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { Link } from "react-router-dom";
import "../styles/ArticleTop.css";

// Custom tooltip component
const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const { id, name, count } = payload[0].payload;

    return (
      <div style={{
        pointerEvents: "auto",
        backgroundColor: "#fff",
        border: "1px solid #ccc",
        padding: "10px",
        borderRadius: "4px",
        boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        maxWidth: "200px",
        minWidth: "200px",
        textAlign: "center",
        transform: "translateX(-50%)",
        marginTop: "-50px"
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
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

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
            angle={isMobile ? -45 : 0} // Adjust angle based on screen width
            textAnchor={isMobile ? "end" : "middle"}  // Adjust text anchor based on screen width
            tick={{
              fontSize: 16,
              dominantBaseline: 'middle'
            }} // Adjust label positioning
            interval={0} // Ensure all labels are rendered
            height={isMobile ? 120 : 100} // Adjust height based on screen width
            style={{
              wordWrap: 'break-word',
              whiteSpace: 'normal',
              overflow: 'hidden',
              maxWidth: '100px' // Add maximum width for wrapping
            }}
          />
          <YAxis />
          <Tooltip 
            content={<CustomTooltip />}
            cursor={{ fill: 'rgba(0, 0, 0, 0.1)' }}
            offset={0}
            allowEscapeViewBox={{ x: true, y: true }}
          />
          <Legend />
          <Bar dataKey="count" fill="#333" name="Número de Artigos" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default SiteCounter;
