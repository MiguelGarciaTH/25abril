import React, { useState } from 'react';
import SearchForm from './SearchForm';
import SearchResults from './SearchResults';

const ParentComponent = () => {
  const [query, setQuery] = useState('');

  const handleSearch = (searchTerm) => {
    setQuery(searchTerm);
  };

  return (
    <div>
      <SearchForm onSearch={handleSearch} />
      {/* Pass the query to the SearchResults component */}
      {query && <SearchResults query={query} />}
    </div>
  );
};

export default ParentComponent;
