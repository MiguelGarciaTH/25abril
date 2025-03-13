import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/SideBar';
import Home from './routes/Home';
import About from './routes/About';
import SearchResults from './components/SearchResults';
import Entities from './routes/Entities';
import Articles from './routes/Articles';
import Data from './routes/Data';

const App = () => {
  return (
    <Router>
      <div className="App">
        <Sidebar />
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/about" element={<About />} />
          <Route path="/home" element={<Home />} />
          <Route path="/data" element={<Data />} />
          <Route path="/entities" element={<Entities />} />
          <Route path="/search-results" element={<SearchResults />} />
          <Route 
            path="/articles/:entityId/:entityName/*" 
            element={
              <Articles />
            } 
          />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
