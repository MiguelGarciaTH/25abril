import React, { forwardRef } from 'react';
import PropTypes from 'prop-types';
import "../styles/EntitySearchForm.css";

const EntitySearchForm = forwardRef(({ value, onChange, clearSearch }, ref) => {
    const handleKeyDown = (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'f') {
            e.preventDefault();
            if (ref?.current) {
                ref.current.focus();
            }
        }
    };

    const handleClear = () => {
        if (ref?.current) {
            ref.current.value = "";
            onChange({ target: { value: "" } });
        }
        if (clearSearch) {
            clearSearch();
        }
    };

    return (
        <div className="search-container">
            <div className="search-bar">
                <div className="input-wrapper">
                    <input
                        ref={ref}
                        type="search"
                        value={value}
                        onChange={onChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Procurar..."
                        className="search-input"
                        autoFocus
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
});

EntitySearchForm.displayName = 'EntitySearchForm';

EntitySearchForm.propTypes = {
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    clearSearch: PropTypes.func,
};

export default EntitySearchForm;