import React, { useState, useEffect, useCallback } from 'react';
import Header from "../components/Header";
import Entity from '../components/Entity';

import "../index.css";

const Entities = () => {
    const [entities, setResults] = useState([]);
    const [loading, setLoading] = useState(true); // Initially true to show loading state on first load
    const [error, setError] = useState(null);
    const [page, setPage] = useState(1); // Page state for pagination

    const fetchData = useCallback(async (pageNumber) => {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity?page=${pageNumber}`);
            if (!response.ok) {
                throw new Error('Failed to fetch data');
            }
            const data = await response.json();
            const entityData = data.content || [];

            // If it's the first page, replace the entities, else append
            setResults((prevEntity) => (pageNumber === 1 ? entityData : [...prevEntity, ...entityData]));

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    }, []);

    // Initial data fetch
    useEffect(() => {
        fetchData(0);
    }, [fetchData]);

    // Scroll event to trigger more data fetch on reaching bottom
    useEffect(() => {
        const handleScroll = () => {
            const bottom = window.innerHeight + document.documentElement.scrollTop >= document.documentElement.offsetHeight - 1;
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
            <div className="polaroid-container">
                {entities.map((entity) => (
                    <Entity key={`${entity.id}-${page}`} entityId={entity.id} entityName={entity.name} entityBio={entity.biography} entityImage={entity.imageUrl} />
                ))}
            </div>
            {loading && page > 0 && <div>Loading more...</div>} {/* Optional: show loader for subsequent pages */}
        </div>
    );
};

export default Entities;
