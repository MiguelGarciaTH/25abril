import React from 'react';
import PropTypes from 'prop-types';
import "../styles/SearchBar.css";

const SearchBar = ({ value, onChange }) => {
    const handleKeyDown = (e) => {
        // Prevent browser's find dialog when pressing Ctrl/Cmd + F
        if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
            e.preventDefault();
        }
    };

    return (
        <div className="search-container">
            <div className="search-bar">
                <input
                    type="search"
                    value={value}
                    onChange={onChange}
                    onKeyDown={handleKeyDown}
                    placeholder="Search..."
                    className="search-input"
                />
            </div>
        </div>
    );
};

SearchBar.propTypes = {
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
};

export default SearchBar;