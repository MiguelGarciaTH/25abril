import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useNavigationType } from 'react-router-dom';
import Header from "../components/Header";
import Entity from '../components/Entity';
import EntitySearchForm from '../components/EntitySearchForm';
import EmptyResults from '../components/EmptyResults';
import "../index.css";
import "../styles/ArticleSearchForm.css";
import "../styles/Entities.css";

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
    const [skipFetch, setSkipFetch] = useState(true); // Start with skip true to prevent initial fetch
    const searchInputRef = useRef(null);
    const navigationType = useNavigationType();
    const isInitialRestore = useRef(true);

    // Fetch entity types for chips
    useEffect(() => {
        const fetchEntityTypes = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_REST_URL}/entity/types`);
                const types = await response.json();
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
            const url = `${import.meta.env.VITE_REST_URL}/entity?page=${pageNumber}&size=12&search_term=${query}&type=${type}`;
            const response = await fetch(url);
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

    // Save both entities and scroll state
    const handleEntityClick = () => {
        sessionStorage.setItem("entities_state", JSON.stringify({
            scrollY: window.scrollY,
            entities: entities,
            page: page,
            selectedType: selectedType
        }));
    };

    // Restore state when navigating back
    useEffect(() => {
        if (navigationType === "POP" && isInitialRestore.current) {
            const savedState = JSON.parse(sessionStorage.getItem("entities_state"));
            if (savedState) {
                setEntities(savedState.entities);
                setPage(savedState.page);
                setSelectedType(savedState.selectedType);
                // Wait for render then scroll
                setTimeout(() => {
                    window.scrollTo(0, savedState.scrollY);
                }, 100);
            }
            isInitialRestore.current = false;
        } else if (navigationType !== "POP") {
            sessionStorage.removeItem("entities_state");
        }
        setSkipFetch(false);
    }, [navigationType]);

    // Single source of truth for fetching
    useEffect(() => {
        if (skipFetch) return;
        console.log("🔍 Fetching with:", { page, searchQuery, selectedType });
        fetchData(page, searchQuery, selectedType);
    }, [fetchData, page, searchQuery, selectedType, skipFetch]);

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

    // Add this new useEffect for focus management
    useEffect(() => {
        if (!typing && searchInputRef.current) {
            searchInputRef.current.focus();
            searchInputRef.current.select();
        }
    }, [typing]);

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
        const newType = type === selectedType ? "" : type;
        setSelectedType(newType);
        setEntities([]);
        setPage(0);
        setHasMoreData(true);
        sessionStorage.setItem("entities_selected_type", newType);
    };

    if (loading && page === 0) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="entities-layout">
            <Header isHome={false} />
            <div className="search-and-chips">
                <EntitySearchForm
                    className="search-form"
                    value={searchQuery}
                    onChange={handleSearchChange}
                    clearSearch={clearSearch}
                    ref={searchInputRef}
                />
                <div className="chip-container">
                    {entityTypes.map((type) => (
                        <div
                            key={type}
                            className={`chip ${selectedType === type ? "chip-selected" : ""}`}
                            onClick={() => handleChipClick(type)}
                        >
                            {type
                                .replace(/_/g, " ") // Replace underscores with spaces
                                .charAt(0)
                                .toUpperCase() + type.replace(/_/g, " ").slice(1).toLowerCase()} {/* Capitalize first letter */}
                        </div>
                    ))}
                </div>
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
                            onClick={handleEntityClick}
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
