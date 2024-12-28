import React from "react";
import "../css/LoadingSpinner.css"; // Add CSS for the spinner

function LoadingSpinner() {
  return (
    <div className="spinner-container">
      <div className="spinner"></div>
      <p className="Sous-Title">Processing, please wait...</p>
    </div>
  );
}

export default LoadingSpinner;
