import React, { useState, useEffect } from "react";
import ArticleTopElement from "./ArticleTopElement";
import EmptyResults from "./EmptyResults";
import "../styles/ArticleTop.css";

const ArticleTop = ({ size, toggle, isMobile }) => {
  const [articles, setArticles] = useState([]);

  useEffect(() => {
    const fetchArticles = async () => {
      const url =
        toggle === "Relevância"
          ? `${import.meta.env.VITE_REST_URL}/articles/stats/top-relevance?size=${size}`
          : `${import.meta.env.VITE_REST_URL}/articles/stats/top-entities?size=${size}`;
      try {
        const response = await fetch(url);
        const data = await response.json();
        setArticles(data);
      } catch (error) {
        console.error("Error fetching articles:", error);
      }
    };

    fetchArticles();
  }, [size, toggle]);

  return (
    <div className={`articleTopContainer ${isMobile ? "mobile" : ""}`}>
      <div className={`articlesWrapper ${isMobile ? "mobile" : ""}`}>
        {articles.length > 0 ? (
          articles.map((article, index) => (
            <ArticleTopElement key={article.id || index} article={article} isMobile={isMobile} />
          ))
        ) : (
          <EmptyResults />
        )}
      </div>
    </div>
  );
};

export default ArticleTop;
