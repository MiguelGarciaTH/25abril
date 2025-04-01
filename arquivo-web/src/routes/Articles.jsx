import React, { useState, useEffect, useCallback } from 'react';
import Header from "../components/Header";
import { useParams } from 'react-router-dom';
import ArticlePreview from "../components/ArticlePreview";
import EmptyResults from "../components/EmptyResults";
import "../index.css";

const Articles = () => {
    const { id, name, path } = useParams();
    const [articles, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(1);

    const fetchData = useCallback(async (pageNumber) => {
        setLoading(true);
        setError(null);

        try {
            const url = `${import.meta.env.VITE_REST_URL}/articles/${path}/${id}?page=${pageNumber}`;
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Failed to fetch data');
            }
            const data = await response.json();
            const articlesData = data.content || [];


            setResults(prevArticles =>
                pageNumber === 1 ? articlesData : [...prevArticles, ...articlesData]
            );
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }, [id]);

    // Initial data fetch
    useEffect(() => {
        setPage(1);
        setResults([]);
        fetchData(1);
    }, [id, fetchData]);

    // Infinite scroll handler
    useEffect(() => {
        const handleScroll = () => {
            if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 100 && !loading) {
                setPage(prevPage => {
                    const nextPage = prevPage + 1;
                    fetchData(nextPage);
                    return nextPage;
                });
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [loading, fetchData]);

    if (loading && page === 1) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <Header isHome={false} />
            <div className="news-paper-head">
                <div className="headerobjectswrapper">
                    <br /><br />
                    <header className="news-paper-header">{name}</header>
                </div>
                <div className="subhead"> </div>
                <div className="content">
                    {articles.length === 0 ? (
                        <EmptyResults />
                    ) : (
                        <div className="collumns">
                            {articles.map((article) => (
                                <ArticlePreview
                                    key={article.articleDetail.id}
                                    item={article}
                                />
                            ))}
                        </div>
                    )}
                </div>
            </div>
            {loading && page > 1 && <div>Loading more...</div>}
        </div>
    );
};

export default Articles;
