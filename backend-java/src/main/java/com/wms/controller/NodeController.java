// controller/NodeController.java
package com.wms.controller;

import com.wms.model.ComputerNode;
import com.wms.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/nodes")
@CrossOrigin(origins = "*")
public class NodeController {

    @Autowired private NodeRepository nodeRepository;

    /** Python agent calls this every few seconds with status */
    @PostMapping("/heartbeat")
    public ComputerNode heartbeat(@RequestBody ComputerNode node) {
        node.setLastUpdated(LocalDateTime.now());
        
        if (node.getCpuUsage() > 80 || node.getMemoryUsage() > 80) {
            node.setStatus("OVERLOADED");
        } else if (node.getCpuUsage() < 30) {
            node.setStatus("IDLE");
        } else {
            node.setStatus("ONLINE");
        }
        
        return nodeRepository.save(node);
    }

    @GetMapping
    public List<ComputerNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ComputerNode getNode(@PathVariable String id) {
        return nodeRepository.findById(id).orElse(null);
    }
}