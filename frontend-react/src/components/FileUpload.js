import React, { useState } from "react";

function FileUpload({ taskId, userId, refreshFiles }) {
  const [file, setFile] = useState(null);

  const uploadFile = async () => {
    if (!file) {
      alert("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("taskId", taskId);
    formData.append("userId", userId);

    try {
      const response = await fetch("http://localhost:8080/api/files/upload", {
        method: "POST",
        body: formData
      });

      if (response.ok) {
        alert("Uploaded!");
        setFile(null);
        refreshFiles(); // 🔥 refresh list after upload
      } else {
        alert("Upload failed");
      }
    } catch (error) {
      console.error(error);
      alert("Error uploading file");
    }
  };

  return (
    <div>
      <input type="file" onChange={(e) => setFile(e.target.files[0])} />
      <p>{file ? file.name : "No file selected"}</p>
      <button onClick={uploadFile}>Upload</button>
    </div>
  );
}

export default FileUpload;