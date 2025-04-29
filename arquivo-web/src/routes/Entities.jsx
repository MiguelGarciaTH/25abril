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
    const [hasMoreData, setHasMoreData] = useState(true);
    const [typing, setTyping] = useState(false);
    const [entityTypes, setEntityTypes] = useState([]); // Store entity types for chips
    const [selectedType, setSelectedType] = useState(""); // Track selected chip filter

    // Fetch entity types for chips
    useEffect(() => {
        const fetchEntityTypes = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity/types`);
                const types = await response.json();
                console.log("Fetched entity types:", types); // Debug log for fetched types
                setEntityTypes(types);
            } catch (error) {
                console.error("Error fetching entity types:", error);
            }
        };
        fetchEntityTypes();
    }, []);

    const fetchData = useCallback(async (pageNumber, query = "", type = "") => {
        if (!hasMoreData) return;

        setLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity?page=${pageNumber}&size=12&search_term=${query}&type=${type}`);
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
        fetchData(page, searchQuery, selectedType);
    }, [fetchData, page, searchQuery, selectedType]);

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
        setTyping(true);

        if (searchTimer) {
            clearTimeout(searchTimer);
        }

        const timer = setTimeout(() => {
            setTyping(false);
            setEntities([]);
            setPage(0);
            setHasMoreData(true);
            fetchData(0, newQuery, selectedType);
        }, 500);

        setSearchTimer(timer);
    };

    const clearSearch = () => {
        setSearchQuery("");
        setEntities([]);
        setPage(0);
        setHasMoreData(true);
    };

    const handleChipClick = (type) => {
        setSelectedType(type === selectedType ? "" : type); // Toggle chip selection
        setEntities([]);
        setPage(0);
        setHasMoreData(true);
    };

    if (loading && page === 0) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <Header isHome={false} />
            <EntitySearchForm
                className="search-form"
                value={searchQuery}
                onChange={handleSearchChange}
                clearSearch={clearSearch}
            />
            <div className="chip-container">
                {entityTypes.map((type) => (
                    <div
                        key={type}
                        className={`chip ${selectedType === type ? "chip-selected" : ""}`}
                        onClick={() => handleChipClick(type)}
                    >
                        {type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()} {/* Capitalize first letter */}
                    </div>
                ))}
            </div>
            {!loading && !typing && entities.length === 0 ? (
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
