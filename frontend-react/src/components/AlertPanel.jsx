import React from "react";

export default function AlertPanel({ alerts }) {
  return (
    <div style={{ marginTop: 30, background: "#1e293b", padding: 20, borderRadius: 12 }}>
      <h2>🚨 Recent Alerts</h2>
      {alerts.length === 0 && <p>No alerts.</p>}
      <ul>
        {alerts.slice(-10).reverse().map(a => (
          <li key={a.id}>
            <strong>{a.employeeName}</strong> — {a.activityType} — {a.description}
            <small style={{ marginLeft: 10, color: "#94a3b8" }}>{a.timestamp}</small>
          </li>
        ))}
      </ul>
    </div>
  );
}