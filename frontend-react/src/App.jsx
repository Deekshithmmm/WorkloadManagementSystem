import React from "react";
import Dashboard from "./components/Dashboard";

export default function App() {
  return (
    <div style={{ fontFamily: "Arial", background: "#0f172a", minHeight: "100vh", color: "#fff" }}>
      <h1 style={{ textAlign: "center", padding: 20 }}>
        🖥️ Workload Management Dashboard
      </h1>
      <Dashboard />
    </div>
  );
}