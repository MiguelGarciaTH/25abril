import React, { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom'; // Use location hook to get query params
import Header from './Header';
import ArticlePreview from './ArticlePreview';
import '../styles/Article.css'; // Import styles

const ArticleSearchResults = () => {
    const [articles, setResults] = useState([]);
    const [loading, setLoading] = useState(true); // Initially true to show loading state on first load
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0); // Page state for pagination
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const query = queryParams.get('query');

    const fetchData = useCallback(async (pageNumber) => {
        if (!query) return; // Skip fetch if no query

        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_REST_URL}/articles?search_term=${query}&page=${pageNumber}`);
            if (!response.ok) {
                throw new Error('Failed to fetch data');
            }
            const data = await response.json();
            const articlesData = data.content || [];

            // If it's the first page, replace the articles, else append
            setResults((prevArticles) => (pageNumber === 0 ? articlesData : [...prevArticles, ...articlesData]));

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }, [query]);

    // Load data on query change
    useEffect(() => {
        if (query) {
            setPage(0); // Reset to the first page whenever the query changes
            fetchData(0); // Fetch the first page of results
        }
    }, [query, fetchData]);

    // Scroll event to trigger more data fetch on reaching bottom
    useEffect(() => {
        const handleScroll = () => {
            const bottom = window.innerHeight + document.documentElement.scrollTop === document.documentElement.offsetHeight;
            if (bottom && !loading) {
                setPage((prevPage) => {
                    const nextPage = prevPage + 1;
                    fetchData(nextPage); // Fetch next page
                    return nextPage;
                });
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [loading, fetchData]);

    if (loading && page === 1) return <div>Loading...</div>; // Loading indicator on first load
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <Header isHome={false} />
            <div className="news-paper-head">
                <div className="headerobjectswrapper">
                    <br /><br />
                    <header className="news-paper-header">{query}</header>
                </div>
                <div className="subhead"> </div>
                <div className="content">
                    <div className="collumns">
                        {articles.map((article) => (
                            <ArticlePreview
                                key={article.articleDetail.id}
                                item={article}
                            />
                        ))}
                    </div>
                </div>
            </div>
            {loading && page > 1 && <div>Loading more...</div>} {/* Optional: show loader for subsequent pages */}
        </div>
    );
};

export default ArticleSearchResults;
