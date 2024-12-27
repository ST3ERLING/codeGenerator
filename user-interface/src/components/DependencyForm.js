import React, { useState } from "react";
import "../css/DependencyForm.css";
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
  const [dependency, setDependency] = useState("");

  const handleAddDependency = () => {
    if (dependency && !selectedDependencies.includes(dependency)) {
      setSelectedDependencies([...selectedDependencies, dependency]);
      setDependency(""); // Reset the dropdown
    }
  };

  const handleRemoveDependency = (depToRemove) => {
    setSelectedDependencies(selectedDependencies.filter((dep) => dep !== depToRemove));
  };

  return (
    <div>
      <h3 className="Sous-Title">Select Dependencies</h3>

      {/* Dropdown for Selecting Dependencies */}
      <div style={{ marginBottom: "15px" }}>
        <select
          className="form-control"
          value={dependency}
          onChange={(e) => setDependency(e.target.value)}
        >
          <option value="" disabled>
            -- Select Dependency --
          </option>
          {COMMON_DEPENDENCIES.filter((dep) => !selectedDependencies.includes(dep)).map(
            (dep, index) => (
              <option key={index} value={dep}>
                {dep}
              </option>
            )
          )}
        </select>
        <button
          className="add-dependency-button "
          onClick={handleAddDependency}
          disabled={!dependency}
        >
          Add Dependency
        </button>
      </div>

      {/* Table of Selected Dependencies */}
      <table className="table">
        <thead>
          <tr>
            <th>Dependency</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {selectedDependencies.length > 0 ? (
            selectedDependencies.map((dep, index) => (
              <tr key={index}>
                <td>{dep}</td>
                <td>
                  <button
                    className="remove-dependency-button"
                    onClick={() => handleRemoveDependency(dep)}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="2" style={{ textAlign: "center" }}>
                No dependencies selected.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default DependencyForm;
