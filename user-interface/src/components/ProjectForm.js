import React, { useState } from 'react';
import axios from 'axios';
import EntityForm from './EntityForm';

function ProjectForm() {
  const [projectName, setProjectName] = useState('');
  const [packageName, setPackageName] = useState('');
  const [javaVersion, setJavaVersion] = useState('11');
  const [bootVersion, setBootVersion] = useState('3.2.0');
  const [projectType, setProjectType] = useState('maven-project');
  const [language, setLanguage] = useState('java');
  const [entities, setEntities] = useState([
    { entityName: '', packageName: '', fields: [{ fieldName: '', fieldType: 'String' }] },
  ]);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const requestData = {
      type: projectType,
      language,
      bootVersion,
      baseDir: projectName,
      groupId: 'com.example',
      artifactId: projectName.toLowerCase().replace(/\s+/g, '-'),
      name: projectName,
      packageName: `com.example.${projectName.toLowerCase().replace(/\s+/g, '')}`, // Default package name
      javaVersion,
      entities: entities.map((entity) => ({
        name: entity.entityName,
        packageName: `${packageName}.entity`,
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
      link.setAttribute('download', `${projectName}.zip`);
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
      <form className="mt-4" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Project Name</label>
          <input
            type="text"
            className="form-control"
            value={projectName}
            onChange={(e) => setProjectName(e.target.value)}
            required
          />
        </div>
        <div className="form-group mt-3">
          <label>Package Name</label>
          <input
            type="text"
            className="form-control"
            value={packageName}
            onChange={(e) => setPackageName(e.target.value)}
            required
          />
        </div>
        <div className="form-group mt-3">
          <label>Java Version</label>
          <select
            className="form-control"
            value={javaVersion}
            onChange={(e) => setJavaVersion(e.target.value)}
          >
            <option value="8">Java 8</option>
            <option value="11">Java 11</option>
            <option value="17">Java 17</option>
            <option value="21">Java 21</option>
            <option value="23">Java 23</option>
          </select>
        </div>
        <div className="form-group mt-3">
          <label>Spring Boot Version</label>
          <input
            type="text"
            className="form-control"
            value={bootVersion}
            onChange={(e) => setBootVersion(e.target.value)}
          />
        </div>
        <div className="form-group mt-3">
          <label>Project Type</label>
          <select
            className="form-control"
            value={projectType}
            onChange={(e) => setProjectType(e.target.value)}
          >
            <option value="maven-project">Maven Project</option>
            <option value="gradle-project">Gradle Project</option>
          </select>
        </div>
        <div className="form-group mt-3">
          <label>Language</label>
          <select
            className="form-control"
            value={language}
            onChange={(e) => setLanguage(e.target.value)}
          >
            <option value="java">Java</option>
            <option value="kotlin">Kotlin</option>
          </select>
        </div>

        <EntityForm entities={entities} setEntities={setEntities} />

        {error && <div className="alert alert-danger mt-3">{error}</div>}

        <button type="submit" className="btn btn-primary mt-3">
          Generate Project
        </button>
      </form>
    </div>
  );
}

export default ProjectForm;
