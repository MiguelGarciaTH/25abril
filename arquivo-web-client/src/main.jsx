import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

import {
  createBrowserRouter,
  RouterProvider
} from 'react-router-dom'


import Home from './routes/Home.jsx'
import Contact from './routes/Contact.jsx'
import ErrorPage from './routes/ErrorPage.jsx'
import Artists from './routes/Artists.jsx'
import Articles from './routes/Articles.jsx'
import Politicians from './routes/Politicians.jsx'
import Parties from './routes/Parties.jsx'
import Events from './routes/Events.jsx'
import Movements from './routes/Movements.jsx'

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
        path: "contact",
        element: <Contact />
      },
      {
        path: "artistas",
        element: <Artists />
      },
      {
        path: "politicos",
        element: <Politicians />
      },
      {
        path: "partidos",
        element: <Parties />
      },
      {
        path: "eventos",
        element: <Events />
      },
      {
        path: "movimentos",
        element: <Movements />
      },
      {
        path: "/articles/:id",
        element: <Articles />
      }
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
