import React from "react";

export default function NodeCard({ node }) {
  const statusColor = {
    ONLINE: "#22c55e",
    IDLE: "#3b82f6",
    OVERLOADED: "#ef4444",
    OFFLINE: "#6b7280"
  }[node.status] || "#6b7280";

  return (
    <div style={{ background: "#1e293b", padding: 16, borderRadius: 12, borderLeft: `6px solid ${statusColor}` }}>
      <h3>{node.nodeId}</h3>
      <p>👤 {node.employeeName} {node.employeePresent ? "🟢 Present" : "🔴 Absent"}</p>
      <p>🎯 Activity: <strong>{node.currentActivity}</strong></p>
      <p>💻 CPU: {node.cpuUsage}% | RAM: {node.memoryUsage}%</p>
      <p>💾 Free: {node.availableMemoryGB} GB</p>
      <p>📊 Status: <span style={{ color: statusColor }}>{node.status}</span></p>
    </div>
  );
}