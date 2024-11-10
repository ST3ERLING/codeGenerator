// App.js
import React from 'react';
import ProjectForm from './components/ProjectForm';
import EntityForm from './components/EntityForm';

function App() {
  return (
    <div className="container mt-5">
      <h1>Spring Boot Project Generator</h1>
      <ProjectForm />
      <EntityForm />
    </div>
  );
}

export default App;
