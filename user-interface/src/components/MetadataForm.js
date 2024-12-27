import React from 'react';

function MetadataForm({ projectDetails, setProjectDetails }) {
  const { projectName, packageName, javaVersion, bootVersion, projectType, language } = projectDetails;

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProjectDetails((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <div>
      <h2 className="Sous-Title">Define Metadata</h2>
      <form className="mt-4">
        <div className="form-group">
          <label style={{ fontWeight: 'bold' }}>Project Name</label>
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
          <label style={{ fontWeight: 'bold' }}>Package Name</label>
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
          <label style={{ fontWeight: 'bold' }}>Java Version</label>
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
          <label style={{ fontWeight: 'bold' }}>Spring Boot Version</label>
          <div style={{ display: 'flex', gap: '15px' }}>
            {['3.4.2 (SNAPSHOT)', '3.4.1', '3.3.8 (SNAPSHOT)', '3.3.7'].map((version) => (
              <div key={version} style={{ marginBottom: '5px' }}>
                <input
                  type="radio"
                  name="bootVersion"
                  value={version}
                  checked={bootVersion === version}
                  onChange={handleInputChange}
                />
                <label style={{ marginLeft: '8px' }}>{version}</label>
              </div>
            ))}
          </div>
        </div>
        <div className="form-group mt-3">
          <label style={{ fontWeight: 'bold' }}>Project Type</label>
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
          <label style={{ fontWeight: 'bold' }}>Language</label>
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

export default MetadataForm;
