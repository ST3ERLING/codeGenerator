import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'; // Import Router components
import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap first
import './index.css'; // Import your custom styles after Bootstrap
import reportWebVitals from './reportWebVitals';
import Login from './components/Login';
import Signup from './components/Signup'; // Import the Registration component
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/app" element={<App />} />
      </Routes>
    </Router>
  </React.StrictMode>
);

reportWebVitals();
