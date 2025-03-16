import React, { useState, useEffect } from "react";
import ArticleTopElement from "./ArticleTopElement";
import "../ArticleTop.css";

const ArticleTop = ({ size }) => {
  const [articles, setArticles] = useState([]);

  const fetchArticles = async () => {
    try {
      const response = await fetch(
        `${import.meta.env.VITE_REST_URL}/articles/stats?size=${size}`
      );
      if (!response.ok) {
        throw new Error("Failed to fetch articles");
      }
      const data = await response.json();
      setArticles(data);
    } catch (error) {
      console.error("Error fetching articles:", error);
    }
  };

  useEffect(() => {
    fetchArticles(); // Fetch articles whenever size changes
  }, [size]); // Dependency array ensures this runs when size changes

  return (
    <div className="articleTopContainer">
      <div className="articlesWrapper">
        {articles.map((article, index) => (
          <ArticleTopElement key={article.id || index} article={article} />
        ))}
      </div>
    </div>
  );
};

export default ArticleTop;
