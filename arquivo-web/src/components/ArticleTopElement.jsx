import React, { useState } from "react";
import { Link } from 'react-router-dom';
import "../styles/ArticleTop.css";

const ArticleTopElement = ({ article, isMobile }) => {
  const [showEntities, setShowEntities] = useState(false);
  const { articleDetail, searchEntityDetails } = article;

  const handleArticleClick = () => {
    setShowEntities(!showEntities);
  };

  return (
    <div className={`container2 ${isMobile ? "mobile" : ""}`}>
      {/* Always render the bannerLink but toggle its visibility */}
      <div className={`bannerLink ${showEntities ? "show" : ""}`}>
        <a href={articleDetail.url} target="_blank" rel="noopener noreferrer">
          <img
            src="/premio-arquivo-pt-2025-banner-pt-1.png"
            alt="Prémio Arquivo.pt 2025"
            className="bannerImage"
          />
        </a>
      </div>

      <div className={`contentWrapper ${isMobile ? "mobile" : ""}`}>
        {/* Article preview */}
        <div className={`articleContainer ${isMobile ? "mobile" : ""}`} onClick={handleArticleClick}>
          <div className="imageWrapper">
            <img
              src={articleDetail.imagePath}
              alt={articleDetail.title}
              className={`articleImage ${isMobile ? "mobile" : ""}`}
            />
          </div>
          <div className="articleContent">
            <h3 className={`articleTitle ${isMobile ? "mobile" : ""}`}>{articleDetail.title}</h3>
            <div className={`articleMeta ${isMobile ? "mobile" : ""}`}>
              <span className="siteName">{articleDetail.site.name}</span>
              <span className="score">Score: {articleDetail.summaryScore}</span>
            </div>
          </div>
        </div>

        {isMobile ? null : <div className="contentSpacer" />}

        {/* Entity container below the articleContainer */}
        <div 
          className={`entityContainer ${
            showEntities && searchEntityDetails.count > 0 ? "show" : ""
          } ${isMobile ? "mobile" : ""}`}
        >
          {showEntities &&
            searchEntityDetails.count > 0 &&
            searchEntityDetails.searchEntityDetail.map((entity, index) => (
              <Link
                to={`/articles/${entity.id}/${entity.name}/entity`}
                key={entity.id || index}
                className="entityCircle" // Apply the entityCircle styles to the Link
              >
                <img
                  src={entity.imageUrl || "/default-entity-image.png"} // Fallback for missing image
                  alt={entity.name || "Unknown Entity"} // Fallback for missing name
                  className="entityImage"
                />
                <div className="entityName">{entity.name || "Unknown Entity"}</div>
              </Link>
              
            ))}
        </div>
      </div>
    </div>
  );
};

export default ArticleTopElement;
