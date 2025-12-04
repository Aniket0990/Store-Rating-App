import React from "react";

export default function InputField({ label, type, name, value, onChange,placeholder, error }) {
  return (
    <div className="input-group">
      <label>{label}</label>
      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required
      />
      {error && <small className="error">{error}</small>}
    </div>
  );
}
