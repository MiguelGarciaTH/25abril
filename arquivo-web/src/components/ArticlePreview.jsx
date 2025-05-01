import { Link } from "react-router-dom";
import React, { useState } from 'react';
import "../styles/Article.css";

function ArticlePreview({ name, item }) {
    const [isHovered, setIsHovered] = useState(false);

    const highlightText = (text, searchTerm) => {
        if (!searchTerm) return text;
        const regex = new RegExp(`(${searchTerm})`, 'gi');
        return text.split(regex).map((part, index) => 
            regex.test(part) ? (
                <span key={index} style={{
                    backgroundColor: 'rgba(255, 255, 0, 0.5)',
                    transition: 'all 0.3s ease',
                    borderRadius: '3px',
                    padding: '0 2px',
                    boxShadow: '0 0 2px rgba(255, 255, 0, 0.8)',
                }}>
                    {part}
                </span>
            ) : part
        );
    };

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
                            <img className="article-preview-popup"
                                src={item.articleDetail.imagePath ? `/${item.articleDetail.imagePath}` : '/fallback-image.png'} 
                                alt={item.articleDetail.title} 
                                style={!item.articleDetail.imagePath ? { filter: 'grayscale(90%)' } : {}}
                            />
                        </div>
                    )}
                </span>
                <p>
                    <span className="headline hl4">{item.articleDetail.site.name}</span>
                    {/*<span className="score">Relevância: {item.articleDetail.summaryScore}</span>*/}
                </p>
            </div>
            <p className="article-summary">
                {highlightText(item.articleDetail.summary, name)}
            </p>
        </div>
    );
}
export default ArticlePreview;