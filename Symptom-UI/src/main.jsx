import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { CustomLoginContext } from './loginContext.jsx'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <CustomLoginContext>
        <App />
      </CustomLoginContext>
  </React.StrictMode>,
)
