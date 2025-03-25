import React, { useRef } from 'react';
import PropTypes from 'prop-types';
import "../styles/EntitySearchForm.css";

const EntitySearchForm = ({ value, onChange, clearSearch }) => {
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

    const handleClear = () => {
        if (inputRef.current) {
            inputRef.current.value = ""; // Clear the input field
            onChange({ target: { value: "" } }); // Trigger the onChange handler with an empty value
        }
        if (clearSearch) {
            clearSearch(); // Call the clearSearch function if provided
        }
    };

    return (
        <div className="search-container">
            <div className="search-bar">
                <div className="input-wrapper">
                    <input
                        ref={inputRef}
                        type="search"
                        value={value}
                        onChange={onChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Procurar..."
                        className="search-input"
                    />
                    {value && (
                        <button
                            type="button"
                            className="clear-button"
                            onClick={handleClear}
                            aria-label="Clear search"
                        >
                            ✕
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

EntitySearchForm.propTypes = {
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    clearSearch: PropTypes.func, // Add clearSearch as an optional prop
};

export default EntitySearchForm;