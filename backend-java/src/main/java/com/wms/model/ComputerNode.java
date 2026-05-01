package com.wms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "computer_node")
public class ComputerNode {

    @Id
    private String nodeId;

    private String ipAddress;
    private String employeeName;

    private double cpuUsage;
    private double memoryUsage;
    private double availableMemoryGB;
    private int activeTasks;

    private boolean employeePresent;
    private boolean employeeActive;

    private String currentActivity;   // WORKING, PHONE, SLEEPING, AWAY

    private LocalDateTime lastUpdated;

    private String status;            // ONLINE, OFFLINE, OVERLOADED, IDLE

    // ✅ REQUIRED FOR TWILIO ALERTS
    private String phoneNumber;

    // ── Default Constructor (IMPORTANT for JPA) ──
    public ComputerNode() {}

    // ── Getters ─────────────────────────────────

    public String getNodeId() { return nodeId; }
    public String getIpAddress() { return ipAddress; }
    public String getEmployeeName() { return employeeName; }
    public double getCpuUsage() { return cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public double getAvailableMemoryGB() { return availableMemoryGB; }
    public int getActiveTasks() { return activeTasks; }
    public boolean isEmployeePresent() { return employeePresent; }
    public boolean isEmployeeActive() { return employeeActive; }
    public String getCurrentActivity() { return currentActivity; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public String getStatus() { return status; }

    // ✅ THIS FIXES YOUR ERROR
    public String getPhoneNumber() { return phoneNumber; }

    // ── Setters ─────────────────────────────────

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public void setAvailableMemoryGB(double availableMemoryGB) { this.availableMemoryGB = availableMemoryGB; }
    public void setActiveTasks(int activeTasks) { this.activeTasks = activeTasks; }
    public void setEmployeePresent(boolean employeePresent) { this.employeePresent = employeePresent; }
    public void setEmployeeActive(boolean employeeActive) { this.employeeActive = employeeActive; }
    public void setCurrentActivity(String currentActivity) { this.currentActivity = currentActivity; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public void setStatus(String status) { this.status = status; }

    // ✅ REQUIRED FOR SETTING PHONE NUMBER
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}