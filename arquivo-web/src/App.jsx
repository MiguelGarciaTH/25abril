import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/SideBar';
import Home from './routes/Home';
import About from './routes/About';
import ArticleSearchResults from './components/ArticleSearchResults';
import Entities from './routes/Entities';
import Articles from './routes/Articles';
import Stats from './routes/Stats';
import Data from './routes/Data';
import Architecture from './routes/Architecture';
import Error404 from './routes/Error404';

const App = () => {
  return (
    <Router>
      <div className="App">
        <Sidebar />
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/about" element={<About />} />
          <Route path="/architecture" element={<Architecture />} />
          <Route path="/home" element={<Home />} />
          <Route path="/stats" element={<Stats />} />
          <Route path="/data" element={<Data />} />
          <Route path="/entities" element={<Entities />} />
          <Route path="/search-results" element={<ArticleSearchResults />} />
          <Route path="/articles/:id/:name/:path/*" element={<Articles />}/>
          <Route path="*" element={<Error404 />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
