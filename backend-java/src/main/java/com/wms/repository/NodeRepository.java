// repository/NodeRepository.java
package com.wms.repository;

import com.wms.model.ComputerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NodeRepository extends JpaRepository<ComputerNode, String> {
    List<ComputerNode> findByStatus(String status);
    List<ComputerNode> findByEmployeePresentTrue();
}