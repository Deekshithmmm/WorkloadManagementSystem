package com.wms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private String taskPayload;

    private double requiredCpu;
    private double requiredMemoryGB;

    private String assignedNodeId;
    private String originNodeId;
    private String originIp;          // ← NEW

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        if ("COMPLETED".equalsIgnoreCase(status)) {
            completedAt = LocalDateTime.now();
        }
    }

    // ── Getters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getTaskPayload() { return taskPayload; }
    public double getRequiredCpu() { return requiredCpu; }
    public double getRequiredMemoryGB() { return requiredMemoryGB; }
    public String getAssignedNodeId() { return assignedNodeId; }
    public String getOriginNodeId() { return originNodeId; }
    public String getOriginIp() { return originIp; }          // ← NEW
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    // ── Setters ────────────────────────────────────────────────────

    public void setId(Long id) { this.id = id; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setTaskPayload(String taskPayload) { this.taskPayload = taskPayload; }
    public void setRequiredCpu(double requiredCpu) { this.requiredCpu = requiredCpu; }
    public void setRequiredMemoryGB(double requiredMemoryGB) { this.requiredMemoryGB = requiredMemoryGB; }
    public void setAssignedNodeId(String assignedNodeId) { this.assignedNodeId = assignedNodeId; }
    public void setOriginNodeId(String originNodeId) { this.originNodeId = originNodeId; }
    public void setOriginIp(String originIp) { this.originIp = originIp; }    // ← NEW
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}