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
import ErrorPage from './routes/ErrorPage.jsx'
import Articles from './routes/Articles.jsx'
import Entity from './routes/Entity.jsx'

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
        path: "/articles/:id",
        element: <Articles />
      },
      {
        path: "/entity/:type",
        element: <Entity />
      }
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
