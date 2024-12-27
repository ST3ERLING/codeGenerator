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

  const handleAddRelationship = (entityIndex) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? {
            ...entity,
            relationships: [
              ...(entity.relationships || []),
              { targetEntity: '', relationType: 'OneToOne' },
            ],
          }
        : entity
    );
    setEntities(updatedEntities);
  };
  
  const handleRelationshipChange = (entityIndex, relationIndex, name, value) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? {
            ...entity,
            relationships: entity.relationships.map((relation, rIdx) =>
              rIdx === relationIndex ? { ...relation, [name]: value } : relation
            ),
          }
        : entity
    );
    setEntities(updatedEntities);
  };
  
  const handleRemoveRelationship = (entityIndex, relationIndex) => {
    const updatedEntities = entities.map((entity, idx) =>
      idx === entityIndex
        ? {
            ...entity,
            relationships: entity.relationships.filter((_, rIdx) => rIdx !== relationIndex),
          }
        : entity
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
      <h2 style={{ fontWeight: 'bold' }}>Define Entities</h2>
      {entities.map((entity, entityIndex) => (
        <div key={entityIndex} className="mt-3 border p-3">
          {/* Entity Name */}
          <div className="form-group">
            <label style={{ fontWeight: 'bold' }}>Entity Name</label>
            <input
              type="text"
              className="form-control"
              value={entity.entityName}
              onChange={(e) => handleEntityChange(entityIndex, 'entityName', e.target.value)}
              required
            />
          </div>
  
          {/* Package Name */}
          <div className="form-group mt-2">
            <label style={{ fontWeight: 'bold' }}>Package Name</label>
            <input
              type="text"
              className="form-control"
              value={entity.packageName}
              onChange={(e) => handleEntityChange(entityIndex, 'packageName', e.target.value)}
            />
          </div>
  
          {/* Fields Section */}
          <h4 className="mt-3" style={{ fontWeight: 'bold' }}>Fields</h4>
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
                className="remove-button"
                onClick={() => handleRemoveField(entityIndex, fieldIndex)}
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            className="add-button"
            onClick={() => handleAddField(entityIndex)}
          >
            Add Field
          </button>
  
          {/* Relationships Section */}
          <h4 className="mt-3" style={{ fontWeight: 'bold' }}>Relationships</h4>
          {(entity.relationships || []).map((relation, relationIndex) => (
            <div key={relationIndex} className="d-flex align-items-center mt-2">
              <select
                className="form-control me-2"
                value={relation.relationType}
                onChange={(e) =>
                  handleRelationshipChange(entityIndex, relationIndex, 'relationType', e.target.value)
                }
              >
                <option value="OneToOne">OneToOne</option>
                <option value="OneToMany">OneToMany</option>
                <option value="ManyToOne">ManyToOne</option>
                <option value="ManyToMany">ManyToMany</option>
              </select>
              <select
                className="form-control me-2"
                value={relation.targetEntity}
                onChange={(e) =>
                  handleRelationshipChange(entityIndex, relationIndex, 'targetEntity', e.target.value)
                }
              >
                <option value="">Select Target Entity</option>
                {entities
                  .filter((_, idx) => idx !== entityIndex)
                  .map((otherEntity, idx) => (
                    <option key={idx} value={otherEntity.entityName}>
                      {otherEntity.entityName}
                    </option>
                  ))}
              </select>
              <button
                type="button"
                className="remove-button"
                onClick={() => handleRemoveRelationship(entityIndex, relationIndex)}
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            className="add-button"
            onClick={() => handleAddRelationship(entityIndex)}
          >
            Add Relationship
          </button>
  
          {/* Remove Entity */}
          <button
            type="button"
            className="remove-button mt-2"
            onClick={() => handleRemoveEntity(entityIndex)}
          >
            Remove Entity
          </button>
        </div>
      ))}
      <button type="button" className="add-button-entity" onClick={handleAddEntity}>
        Add Entity
      </button>
    </div>
  );
  
}

export default EntityForm;
