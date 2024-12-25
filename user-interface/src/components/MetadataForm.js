import React, { useState } from 'react';

function ProjectForm({ projectDetails, setProjectDetails }) {
  const { projectName, packageName, javaVersion, bootVersion, projectType, language } = projectDetails;

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProjectDetails((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <div>
      <form className="mt-4">
        <div className="form-group">
          <label>Project Name</label>
          <input
            type="text"
            className="form-control"
            name="projectName"
            value={projectName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group mt-3">
          <label>Package Name</label>
          <input
            type="text"
            className="form-control"
            name="packageName"
            value={packageName}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group mt-3">
          <label>Java Version</label>
          <select
            className="form-control"
            name="javaVersion"
            value={javaVersion}
            onChange={handleInputChange}
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
            name="bootVersion"
            value={bootVersion}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group mt-3">
          <label>Project Type</label>
          <select
            className="form-control"
            name="projectType"
            value={projectType}
            onChange={handleInputChange}
          >
            <option value="maven-project">Maven Project</option>
            <option value="gradle-project">Gradle Project</option>
          </select>
        </div>
        <div className="form-group mt-3">
          <label>Language</label>
          <select
            className="form-control"
            name="language"
            value={language}
            onChange={handleInputChange}
          >
            <option value="java">Java</option>
            <option value="kotlin">Kotlin</option>
          </select>
        </div>
      </form>
    </div>
  );
}

export default ProjectForm;
