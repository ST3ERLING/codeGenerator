// App.js
import React from 'react';
import ProjectForm from './components/ProjectForm';
import ChatSupport from './components/ChatSupport';
function App() {
  return (
    <div className="container mt-5">
      <div className="content">
      <h1 className="Title">Spring Boot Project Generator</h1>
      <div className="form-container">
      <ProjectForm />
      </div>
      </div>
      <ChatSupport />
    </div>
  );
}

export default App;
