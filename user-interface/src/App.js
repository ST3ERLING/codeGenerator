// App.js
import React from 'react';
import ProjectForm from './components/ProjectForm';
function App() {
  return (
    <div className="container mt-5">
      <div className="content">
      <h1>Spring Boot Project Generator</h1>
      <div className="form-container">
      <ProjectForm />
      </div>
      </div>
    </div>
  );
}

export default App;
