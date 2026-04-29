import React, { useEffect, useState } from "react";

function FileList({ taskId }) {
  const [files, setFiles] = useState([]);

  const fetchFiles = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/files/task/${taskId}`);
      const data = await res.json();
      setFiles(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [taskId]);

  const downloadFile = (id) => {
    window.open(`http://localhost:8080/api/files/download/${id}`);
  };

  return (
    <div>
      <h3>Files</h3>
      {files.length === 0 ? (
        <p>No files uploaded</p>
      ) : (
        <ul>
          {files.map((file) => (
            <li key={file.id}>
              {file.fileName}
              <button onClick={() => downloadFile(file.id)}>
                Download
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default FileList;