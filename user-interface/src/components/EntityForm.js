// components/EntityForm.js
import React, { useState } from 'react';

function EntityForm() {
  const [entities, setEntities] = useState([]);
  const [entityName, setEntityName] = useState('');
  const [fields, setFields] = useState([{ fieldName: '', fieldType: 'String' }]);

  const handleAddEntity = () => {
    setEntities([...entities, { entityName, fields }]);
    setEntityName('');
    setFields([{ fieldName: '', fieldType: 'String' }]);
  };

  const handleAddField = () => {
    setFields([...fields, { fieldName: '', fieldType: 'String' }]);
  };

  const handleFieldChange = (index, name, value) => {
    const updatedFields = fields.map((field, idx) =>
      idx === index ? { ...field, [name]: value } : field
    );
    setFields(updatedFields);
  };

  return (
    <div className="mt-4">
      <h2>Define Entities</h2>
      <div className="form-group">
        <label>Entity Name</label>
        <input
          type="text"
          className="form-control"
          value={entityName}
          onChange={(e) => setEntityName(e.target.value)}
          required
        />
      </div>

      <h3 className="mt-3">Fields</h3>
      {fields.map((field, index) => (
        <div key={index} className="d-flex align-items-center mt-2">
          <input
            type="text"
            className="form-control me-2"
            placeholder="Field Name"
            value={field.fieldName}
            onChange={(e) => handleFieldChange(index, 'fieldName', e.target.value)}
          />
          <select
            className="form-control me-2"
            value={field.fieldType}
            onChange={(e) => handleFieldChange(index, 'fieldType', e.target.value)}
          >
            <option value="String">String</option>
            <option value="Integer">Integer</option>
            <option value="Long">Long</option>
            <option value="Boolean">Boolean</option>
          </select>
        </div>
      ))}
      <button type="button" className="btn btn-secondary mt-3" onClick={handleAddField}>
        Add Field
      </button>
      <button type="button" className="btn btn-primary mt-3" onClick={handleAddEntity}>
        Save Entity
      </button>
    </div>
  );
}

export default EntityForm;
