import React, { useState } from 'react';
import ProjectForm from './ProjectForm';
import EntityForm from './EntityForm';
import axios from 'axios';

function ProjectForm() {
  const [projectDetails, setProjectDetails] = useState({
    projectName: '',
    packageName: '',
    javaVersion: '11',
    bootVersion: '3.2.0',
    projectType: 'maven-project',
    language: 'java',
  });
  const [entities, setEntities] = useState([
    { entityName: '', packageName: '', fields: [{ fieldName: '', fieldType: 'String' }] },
  ]);
  const [error, setError] = useState(null);

  const handleGenerateProject = async () => {
    const requestData = {
      ...projectDetails,
      entities: entities.map((entity) => ({
        name: entity.entityName,
        packageName: `${projectDetails.packageName}.entity`,
        fields: entity.fields.map((field) => ({
          name: field.fieldName,
          type: field.fieldType,
        })),
      })),
    };

    try {
      const response = await axios.post('http://localhost:8080/generate-project', requestData, {
        headers: {
          'Content-Type': 'application/json',
        },
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${projectDetails.projectName}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error generating project:', error);
      setError('Failed to generate project. Please try again.');
    }
  };

  return (
    <div>
      <MetadataForm projectDetails={projectDetails} setProjectDetails={setProjectDetails} />
      <EntityForm entities={entities} setEntities={setEntities} />

      {error && <div className="alert alert-danger mt-3">{error}</div>}

      <button
        type="button"
        className="btn btn-primary mt-3"
        onClick={handleGenerateProject}
      >
        Generate Project
      </button>
    </div>
  );
}

export default ProjectForm;
