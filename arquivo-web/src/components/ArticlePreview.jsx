import { Link } from "react-router-dom";
import React, { useState } from 'react';
import "../styles/Article.css";

function ArticlePreview({ item }) {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <div className="collumn">
            <div
                className="head"
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                style={{ position: 'relative' }}
            >
                <span className="headline hl3">
                    <Link className="headline hl3" to={item.articleDetail.url}>{item.articleDetail.title}</Link>
                    {isHovered && (
                        <div className="article-preview">
                            <img 
                                src={item.articleDetail.imagePath ? `/${item.articleDetail.imagePath}` : '/fallback-image.png'} 
                                alt={item.articleDetail.title} 
                                className="article-preview-popup" 
                            />
                        </div>
                    )}
                </span>
                <p>
                    <span className="headline hl4">{item.articleDetail.site.name}</span>
                    <span className="score">Relevância: {item.articleDetail.summaryScore}</span>
                </p>
            </div>
            <p className="article-summary">
                {item.articleDetail.summary}
            </p>
        </div>
    );
}
export default ArticlePreview;