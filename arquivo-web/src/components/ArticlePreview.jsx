import { Link } from "react-router-dom";
import React, { useState } from 'react';
import "../styles/Article.css";

function ArticlePreview({ item }) {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <div class="collumn">
            <div
                className="head"
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                style={{ position: 'relative' }}
            >
                <span class="headline hl3">
                    <Link class="headline hl3"
                        to={item.articleDetail.url}
                    >{item.articleDetail.title}</Link>
                    {isHovered && (
                        <div class="article-preview">
                            <img src={`${item.articleDetail.imagePath}`} alt={item.articleDetail.title} class="article-preview-popup" />
                        </div>
                    )}
                </span>
                <p>
                    <span class="headline hl4">{item.articleDetail.site.name}</span>
                    Relevância: {item.articleDetail.summaryScore}
                </p>
            </div>
            <p className="article-summary">
                {item.articleDetail.summary}
            </p>
        </div>
    );
}
export default ArticlePreview;