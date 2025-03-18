import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import the useNavigate hook
import "../styles/SearchForm.css"; // Optional for custom styling

const SearchForm = () => {
  const [query, setQuery] = useState('');
  const navigate = useNavigate(); // Initialize the navigate function

  const handleChange = (e) => setQuery(e.target.value);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      // Navigate to the new page with the query as a query parameter
      navigate(`/search-results?query=${query.trim()}`);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="search-form">
      <input
        type="text"
        value={query}
        onChange={handleChange}
        placeholder="Search"
        className="search-input"
      />
      <button type="submit">
        <img src="./android-chrome-512x512.png" alt="Search" />
      </button>
    </form>
  );
};

export default SearchForm;
