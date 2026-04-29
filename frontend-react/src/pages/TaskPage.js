import React from "react";
import FileUpload from "../components/FileUpload";
import FileList from "../components/FileList";

function TaskPage() {
  const taskId = 1;   // 🔥 replace dynamically later
  const userId = 1;

  const refreshFiles = () => {
    window.location.reload(); // simple refresh
  };

  return (
    <div>
      <h2>Task Files</h2>

      <FileUpload
        taskId={taskId}
        userId={userId}
        refreshFiles={refreshFiles}
      />

      <FileList taskId={taskId} />
    </div>
  );
}

export default TaskPage;