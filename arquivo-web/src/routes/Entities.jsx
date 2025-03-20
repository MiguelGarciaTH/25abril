import React, { useState, useEffect, useCallback } from 'react';
import Header from "../components/Header";
import Entity from '../components/Entity';
import EntitySearchForm from '../components/EntitySearchForm';

import "../index.css";
import "../styles/ArticleSearchForm.css";

const Entities = () => {
    const [entities, setEntities] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchTimer, setSearchTimer] = useState(null);

    const fetchData = useCallback(async (pageNumber, query = "") => {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity?page=${pageNumber}&size=10?&search_term=${query}`);
            if (!response.ok) {
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
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchData(page, searchQuery);
    }, [fetchData, page, searchQuery]);

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && !loading) {
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
    }, [loading]);

    const handleSearchChange = (e) => {
        const newQuery = e.target.value;
        setSearchQuery(newQuery);

        if (searchTimer) {
            clearTimeout(searchTimer);
        }

        const timer = setTimeout(() => {
            setEntities([]);
            setPage(0);
            fetchData(0, newQuery);
        }, 500);

        setSearchTimer(timer);
    };

    // Handle search input focus and keydown events
    const handleSearchInput = (e) => {
        // Prevent the browser find-in-page functionality for "Ctrl+F" and "Cmd+F"
        if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
            e.preventDefault(); // Prevent the default find action
        }
    };

    if (loading && page === 0) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <Header isHome={false} />
            <EntitySearchForm  className="search-form"
                value={searchQuery}
                onChange={handleSearchChange}
                onKeyDown={handleSearchInput} // Prevent the find-in-page when typing
            />
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
            <div id="sentinel" style={{ height: '1px', background: 'transparent' }} />
            {loading && page > 0 && <div>Loading more...</div>}
        </div>
    );
};

export default Entities;
