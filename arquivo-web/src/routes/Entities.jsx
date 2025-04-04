import React, { useState, useEffect, useCallback } from 'react';
import Header from "../components/Header";
import Entity from '../components/Entity';
import EntitySearchForm from '../components/EntitySearchForm';
import EmptyResults from '../components/EmptyResults';

import "../index.css";
import "../styles/ArticleSearchForm.css";

const Entities = () => {
    const [entities, setEntities] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchTimer, setSearchTimer] = useState(null);
    const [hasMoreData, setHasMoreData] = useState(true); // Track if more data is available

    const fetchData = useCallback(async (pageNumber, query = "") => {
        if (!hasMoreData) return; // Stop fetching if no more data is available

        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity?page=${pageNumber}&size=12&search_term=${query}`);
            if (!response.ok) {
                <EmptyResults />
                throw new Error('Failed to fetch data');
            }
            const data = await response.json();
            const entityData = data.content || [];

            setEntities((prevEntities) => {
                if (pageNumber === 0) {
                    return entityData;
                }
                return [...prevEntities, ...entityData];
            });

            // Update hasMoreData based on whether new data was returned
            if (entityData.length === 0) {
                setHasMoreData(false);
            }
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }, [hasMoreData]);

    useEffect(() => {
        fetchData(page, searchQuery);
    }, [fetchData, page, searchQuery]);

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && !loading && hasMoreData) {
                    setPage((prevPage) => prevPage + 1);
                }
            },
            {
                rootMargin: '100px',
            }
        );

        const sentinel = document.getElementById('sentinel');
        if (sentinel) {
            observer.observe(sentinel);
        }

        return () => {
            if (sentinel) {
                observer.unobserve(sentinel);
            }
        };
    }, [loading, hasMoreData]);

    const handleSearchChange = (e) => {
        const newQuery = e.target.value;
        setSearchQuery(newQuery);

        if (searchTimer) {
            clearTimeout(searchTimer);
        }

        const timer = setTimeout(() => {
            setEntities([]);
            setPage(0);
            setHasMoreData(true); // Reset hasMoreData for new search
            fetchData(0, newQuery);
        }, 500);

        setSearchTimer(timer);
    };

    const clearSearch = () => {
        setSearchQuery("");
        setEntities([]);
        setPage(0);
        setHasMoreData(true); // Reset hasMoreData for cleared search
    };

    if (loading && page === 0) return <div>Loading...</div>;
    if (error) return <div>Error: {error}                 <EmptyResults />
    </div>;

    return (
        <div>
            <Header isHome={false} />
            <EntitySearchForm
                className="search-form"
                value={searchQuery}
                onChange={handleSearchChange}
                clearSearch={clearSearch}
            />
            {!loading && entities.length === 0 ? (
                <EmptyResults />
            ) : (
                <div className="polaroid-container">
                    {entities.map((entity) => (
                        <Entity
                            key={entity.id}
                            entityId={entity.id}
                            entityName={entity.name}
                            entityBio={entity.biography}
                            entityImage={entity.imageUrl}
                        />
                    ))}
                </div>
            )}
            <div id="sentinel" style={{ height: '1px', background: 'transparent' }} />
            {loading && page > 0 && <div>Loading more...</div>}
        </div>
    );
};

export default Entities;
