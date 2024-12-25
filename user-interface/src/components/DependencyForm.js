import React, { useState } from "react";

const COMMON_DEPENDENCIES = [
  "Spring Web",
  "Spring Data JPA",
  "Spring Security",
  "Thymeleaf",
  "Validation",
  "Spring Test",
  "Mail",
  "MySQL Connector",
  "Actuator",
  "Cache",
];

function DependencyForm({ selectedDependencies, setSelectedDependencies }) {
  const handleDependencyToggle = (dependency) => {
    const exists = selectedDependencies.includes(dependency);

    if (exists) {
      // Remove dependency if it exists
      setSelectedDependencies(selectedDependencies.filter((dep) => dep !== dependency));
    } else {
      // Add dependency
      setSelectedDependencies([...selectedDependencies, dependency]);
    }
  };

  return (
    <div>
      <h3>Select Dependencies</h3>
      <div className="dependency-list">
        {COMMON_DEPENDENCIES.map((dependency, index) => (
          <div key={index} className="dependency-item">
            <input
              type="checkbox"
              checked={selectedDependencies.includes(dependency)}
              onChange={() => handleDependencyToggle(dependency)}
            />
            <label>{dependency}</label>
          </div>
        ))}
      </div>
    </div>
  );
}

export default DependencyForm;
