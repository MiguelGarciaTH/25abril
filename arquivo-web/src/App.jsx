import React from 'react';
import Sidebar from './components/SideBar';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './routes/Home';
import About from './routes/About';
import SearchResults from './components/SearchResults';
import Entities from './routes/Entities';
import Articles from './routes/Articles';  // Add this import

const App = () => {

  return (

    <Router>
      <div className="App">
        <Sidebar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<About />} />
            <Route path="/home" element={<Home />} />
            <Route path="/entities" element={<Entities />} />
            <Route path="/search-results" element={<SearchResults />} />
            <Route path="/articles/:entityId" element={<Articles />} />
          </Routes>
      </div>
    </Router>
  );
};

export default App;
