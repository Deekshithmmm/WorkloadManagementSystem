import React, { useEffect, useState } from "react";
import axios from "axios";
import NodeCard from "./NodeCard";
import AlertPanel from "./AlertPanel";

export default function Dashboard() {
  const [nodes, setNodes] = useState([]);
  const [alerts, setAlerts] = useState([]);

  const fetchData = async () => {
    try {
      const n = await axios.get("http://localhost:8080/api/nodes");
      const a = await axios.get("http://localhost:8080/api/monitoring/activities");
      setNodes(n.data);
      setAlerts(a.data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchData();
    const i = setInterval(fetchData, 5000);
    return () => clearInterval(i);
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(300px,1fr))", gap: 20 }}>
        {nodes.map((n) => <NodeCard key={n.nodeId} node={n} />)}
      </div>
      <AlertPanel alerts={alerts} />
    </div>
  );
}