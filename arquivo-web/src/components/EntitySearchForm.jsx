import React, { useRef } from 'react';
import PropTypes from 'prop-types';
import "../styles/EntitySearchForm.css";

const EntitySearchForm = ({ value, onChange }) => {
    const inputRef = useRef(null);

    const handleKeyDown = (e) => {
        // Prevent browser's find dialog only when pressing Ctrl/Cmd + F
        if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'f') {
            e.preventDefault();
            // Ensure the input retains focus
            if (inputRef.current) {
                inputRef.current.focus();
            }
        }
    };

    return (
        <div className="search-container">
            <div className="search-bar">
                <input
                    ref={inputRef}
                    type="search"
                    value={value}
                    onChange={onChange}
                    onKeyDown={handleKeyDown}
                    placeholder="Procurar..."
                    className="search-input"
                />
            </div>
        </div>
    );
};

EntitySearchForm.propTypes = {
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
};

export default EntitySearchForm;