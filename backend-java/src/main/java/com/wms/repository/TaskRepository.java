package com.wms.repository;

import com.wms.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 🔹 Get tasks by status (PENDING, RUNNING, COMPLETED)
    List<Task> findByStatus(String status);

    // 🔹 Get tasks assigned to a specific node
    List<Task> findByAssignedNodeId(String nodeId);

    // 🔹 Get tasks created by a specific node
    List<Task> findByOriginNodeId(String nodeId);

    // 🔹 Get tasks by status AND node
    List<Task> findByStatusAndAssignedNodeId(String status, String nodeId);

    // 🔹 Get unassigned tasks
    List<Task> findByAssignedNodeIdIsNull();

}