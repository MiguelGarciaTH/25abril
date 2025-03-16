import React, { useState } from "react";
import "../ArticleTop.css";

const ArticleTopElement = ({ article }) => {
  const [showEntities, setShowEntities] = useState(false);
  const { articleDetail, searchEntityDetails } = article;

  const handleArticleClick = () => {
    setShowEntities(!showEntities);
    console.log("Search Entity Count:", searchEntityDetails.count); // Log the count
    console.log("Search Entity Details:", searchEntityDetails.searchEntityDetail); // Log the array
  };

  return (
    <div className="container2">
      <div className="contentWrapper">
        {/* Article preview */}
        <div className="articleContainer" onClick={handleArticleClick}>
          <img
            src={articleDetail.imagePath}
            alt={articleDetail.title}
            className="articleImage"
          />
          <div className="articleContent">
            <h3 className="articleTitle">{articleDetail.title}</h3>
            <div className="articleMeta">
              <span className="siteName">{articleDetail.site.name}</span>
              <span className="score">Score: {articleDetail.contextualScore}</span>
            </div>
          </div>
        </div>

        {/* Show search entities on the right */}
        <div
          className={`entityContainer ${showEntities && searchEntityDetails.count > 0 ? "show" : ""}`}
        >
          {showEntities &&
            searchEntityDetails.count > 0 &&
            searchEntityDetails.searchEntityDetail.map((entity, index) => (
              <div key={entity.id || index} className="entityCircle">
                <img
                  src={entity.imageUrl || "/default-entity-image.png"} // Fallback for missing image
                  alt={entity.name || "Unknown Entity"} // Fallback for missing name
                  className="entityImage"
                />
                <div className="entityName">{entity.name || "Unknown Entity"}</div>
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};

export default ArticleTopElement;
