import React, { useState } from 'react';
import ArticleSearchForm from './ArticleSearchForm';
import ArticleSearchResults from './ArticleSearchResults';

const ParentComponent = () => {
  const [query, setQuery] = useState('');

  const handleSearch = (searchTerm) => {
    setQuery(searchTerm);
  };

  return (
    <div>
      <ArticleSearchForm onSearch={handleSearch} />
      {/* Pass the query to the SearchResults component */}
      {query && <ArticleSearchResults query={query} />}
    </div>
  );
};

export default ParentComponent;
