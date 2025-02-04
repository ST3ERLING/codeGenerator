import React, { useState } from 'react';
import MetadataForm from './MetadataForm';
import EntityForm from './EntityForm';
import LoadingSpinner from './LoadingSpinner';
import DependencyForm from './DependencyForm'; // Import DependencyForm
import axios from 'axios';

function ProjectForm() {
  const [step, setStep] = useState(1); // Track the current step
  const [projectDetails, setProjectDetails] = useState({
    projectName: '',
    packageName: '',
    javaVersion: '11',
    bootVersion: '3.4.1',
    projectType: 'maven-project',
    language: 'java',
  });
  const [entities, setEntities] = useState([
    { entityName: '', packageName: '', fields: [{ fieldName: '', fieldType: 'String' }] },
  ]);
  const [selectedDependencies, setSelectedDependencies] = useState([]); // Define selectedDependencies
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleNext = () => {
    if (step === 1 && !projectDetails.projectName) {
      setError('Project Name is required');
      return;
    }
    setError(null);
    setStep((prevStep) => prevStep + 1);
  };

  const handleBack = () => {
    setStep((prevStep) => prevStep - 1);
  };

  const handleGenerateProject = async () => {
    const requestData = {
      type: projectDetails.projectType || 'maven-project',
      language: projectDetails.language || 'java',
      bootVersion: projectDetails.bootVersion || '3.4.1',
      baseDir: projectDetails.projectName || 'default-project',
      groupId: 'com.example',
      artifactId: projectDetails.projectName?.toLowerCase().replace(/\s+/g, '-') || 'default-project',
      name: projectDetails.projectName || 'default-project',
      packageName: projectDetails.packageName || 'com.example',
      javaVersion: projectDetails.javaVersion || '11',
      entities: entities.map((entity) => ({
        name: entity.entityName,
        packageName: `${projectDetails.packageName}.entity`,
        fields: entity.fields.map((field) => ({
          name: field.fieldName,
          type: field.fieldType,
        })),
        relationships: (entity.relationships || []).map((relation) => ({
          targetEntity: relation.targetEntity,
          relationType: relation.relationType,
        })),
      })),
      dependencies: selectedDependencies,
    };
    setIsLoading(true);
    console.log('Payload:', requestData);
  
    try {
      const response = await axios.post('http://localhost:8888/code-generator/generate-project', requestData, {
        headers: {
          'Content-Type': 'application/json',
        },
        responseType: 'blob',
      });
  
      console.log('Blob response:', response.data);
  
      // Create a URL for the blob
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/zip' }));
      const link = document.createElement('a');
      link.href = url;
  
      // Ensure filename ends with .zip
      link.setAttribute('download', `${projectDetails.projectName || 'default-project'}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error generating project:', error.response?.data || error.message);
      setError('Failed to generate project. Please try again.');
    } finally {
      setIsLoading(false); // Hide spinner
    }
  };
  
  return (
    <div>
      {isLoading && <LoadingSpinner />} {/* Display spinner when isLoading is true */}
      {step === 1 && (
        <MetadataForm projectDetails={projectDetails} setProjectDetails={setProjectDetails} />
      )}

      {step === 2 && <EntityForm entities={entities} setEntities={setEntities} />}

      {step === 3 && (
        <DependencyForm
          selectedDependencies={selectedDependencies}
          setSelectedDependencies={setSelectedDependencies}
        />
      )}

      {error && <div className="alert alert-danger mt-3">{error}</div>}

      <div className="navigation-buttons mt-3">
        {step > 1 && (
          <button className="back-button" onClick={handleBack}>
            Back
          </button>
        )}
        {step < 3 && (
          <button className="next-button" onClick={handleNext}>
            Next
          </button>
        )}
        {step === 3 && (
          <button className="ganerate-button" onClick={handleGenerateProject}>
            Generate Project
          </button>
        )}
        <div className="steps">
          <span className={step === 1 ? 'active-step' : ''}>1</span>
          <span className={step === 2 ? 'active-step' : ''}>2</span>
          <span className={step === 3 ? 'active-step' : ''}>3</span>
        </div>
      </div>
    </div>
  );
}

export default ProjectForm;
