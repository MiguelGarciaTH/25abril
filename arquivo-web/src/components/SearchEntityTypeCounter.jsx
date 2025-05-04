import React, { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { Link } from "react-router-dom";
import "../styles/ArticleTop.css";

const formatType = (type) => {
  return type
    .replace(/_/g, " ")
    .charAt(0)
    .toUpperCase() + type.replace(/_/g, " ").slice(1).toLowerCase();
};

const SearchEntityTypeCounter = () => {
  const [data, setData] = useState([]);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 768);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Custom tooltip component moved inside to access data
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const { id, type, count } = payload[0].payload;

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
          <Link to={`/articles/${id}/${type}/type`} className="customToolTipLink">
            <p style={{ fontWeight: 'bold' }}>{formatType(type)}</p>
            <p>Artigos: {count}</p>
          </Link>
        </div>
      );
    }
    return null;
  };

  // Fetch data from the API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity/type/stats`);
        const result = await response.json();
        // Transform the data to include id and name fields
        const transformedData = result.map((item, index) => ({
          id: index + 1,
          type: item.type,
          count: item.count
        }));
        setData(transformedData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  return (
    <div style={{ width: "90%", height: 400 }}>
      <h3 style={{ textAlign: 'center', marginBottom: '20px' }}>Artigos por categoria</h3>
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="type"
            tickFormatter={formatType}
            angle={isMobile ? -45 : 0}
            textAnchor={isMobile ? "end" : "middle"}
            tick={{
              fontSize: 14,
              dominantBaseline: 'middle'
            }}
            interval={0}
            height={isMobile ? 120 : 100}
            style={{
              wordWrap: 'break-word',
              whiteSpace: 'normal',
              overflow: 'hidden',
              maxWidth: '100px'
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
          <Bar
            dataKey="count"
            fill="#333"
            name="Número de Artigos"
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default SearchEntityTypeCounter;
