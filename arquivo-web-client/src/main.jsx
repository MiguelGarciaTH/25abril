import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

import {
  createBrowserRouter,
  RouterProvider
} from 'react-router-dom'


import Home from './routes/Home.jsx'
import About from './routes/About.jsx'
import Architecture from './routes/Architecture.jsx'
import ErrorPage from './routes/ErrorPage.jsx'
import Articles from './components/Articles.jsx'
import Article from './components/Article.jsx'
import Entities from './components/Entities.jsx'

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/",
        element: <Home />
      },
      {
        path: "about",
        element: <About />
      },
      {
        path: "architecture",
        element: <Architecture />
      },
      {
        path: "/articles/:id/:name",
        element: <Articles />
      },
      {
        path: "/article/:articleId/:entityId",
        element: <Article />
      },
      {
        path: "/entity/:type",
        element: <Entities />
      }
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  
    <RouterProvider router={router} />
  
);
