// components/EntityForm.js
import React, { useState } from 'react';

function EntityForm({ entities = [], setEntities }) {
  const handleAddEntity = () => {
    setEntities([
      ...entities,
      { entityName: '', packageName: '', fields: [{ fieldName: '', fieldType: 'String' }] },
    ]);
  };

  const handleEntityChange = (index, name, value) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === index ? { ...entity, [name]: value } : entity
    );
    setEntities(updatedEntities);
  };

  const handleAddField = (entityIndex) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? { ...entity, fields: [...entity.fields, { fieldName: '', fieldType: 'String' }] }
        : entity
    );
    setEntities(updatedEntities);
  };

  const handleFieldChange = (entityIndex, fieldIndex, name, value) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? {
            ...entity,
            fields: entity.fields.map((field, fIdx) =>
              fIdx === fieldIndex ? { ...field, [name]: value } : field
            ),
          }
        : entity
    );
    setEntities(updatedEntities);
  };

  const handleRemoveField = (entityIndex, fieldIndex) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? { ...entity, fields: entity.fields.filter((_, fIdx) => fIdx !== fieldIndex) }
        : entity
    );
    setEntities(updatedEntities);
  };

  const handleRemoveEntity = (index) => {
    setEntities(entities.filter((_, idx) => idx !== index));
  };

  return (
    <div className="mt-4">
      <h2>Define Entities</h2>
      {entities.map((entity, entityIndex) => (
        <div key={entityIndex} className="mt-3 border p-3">
          <div className="form-group">
            <label>Entity Name</label>
            <input
              type="text"
              className="form-control"
              value={entity.entityName}
              onChange={(e) => handleEntityChange(entityIndex, 'entityName', e.target.value)}
              required
            />
          </div>
          <div className="form-group mt-2">
            <label>Package Name</label>
            <input
              type="text"
              className="form-control"
              value={entity.packageName}
              onChange={(e) => handleEntityChange(entityIndex, 'packageName', e.target.value)}
            />
          </div>

          <h4 className="mt-3">Fields</h4>
          {entity.fields.map((field, fieldIndex) => (
            <div key={fieldIndex} className="d-flex align-items-center mt-2">
              <input
                type="text"
                className="form-control me-2"
                placeholder="Field Name"
                value={field.fieldName}
                onChange={(e) =>
                  handleFieldChange(entityIndex, fieldIndex, 'fieldName', e.target.value)
                }
              />
              <select
                className="form-control me-2"
                value={field.fieldType}
                onChange={(e) =>
                  handleFieldChange(entityIndex, fieldIndex, 'fieldType', e.target.value)
                }
              >
                <option value="String">String</option>
                <option value="Integer">Integer</option>
                <option value="Long">Long</option>
                <option value="Boolean">Boolean</option>
              </select>
              <button
                type="button"
                className="btn btn-danger btn-sm"
                onClick={() => handleRemoveField(entityIndex, fieldIndex)}
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            className="btn btn-secondary mt-2"
            onClick={() => handleAddField(entityIndex)}
          >
            Add Field
          </button>
          <button
            type="button"
            className="btn btn-danger mt-2 ms-2"
            onClick={() => handleRemoveEntity(entityIndex)}
          >
            Remove Entity
          </button>
        </div>
      ))}
      <button type="button" className="btn btn-primary mt-4" onClick={handleAddEntity}>
        Add Entity
      </button>
    </div>
  );
}

export default EntityForm;
