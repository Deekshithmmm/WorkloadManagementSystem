package com.wms.repository;

import com.wms.model.ComputerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<ComputerNode, String> {

    // ✅ Find nodes by status (ONLINE, OFFLINE, etc.)
    List<ComputerNode> findByStatus(String status);

    // ✅ Get only nodes where employee is present
    List<ComputerNode> findByEmployeePresentTrue();

    // ✅ Get nodes by employee name
    List<ComputerNode> findByEmployeeName(String employeeName);

    // ✅ Get nodes where activity is PHONE / SLEEPING etc.
    List<ComputerNode> findByCurrentActivity(String currentActivity);
}