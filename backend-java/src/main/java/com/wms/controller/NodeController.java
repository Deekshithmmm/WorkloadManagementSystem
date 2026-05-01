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

    @Autowired
    private NodeRepository nodeRepository;

    // ✅ 1. CREATE or UPDATE node manually (includes phone number)
    @PostMapping
    public ComputerNode createOrUpdateNode(@RequestBody ComputerNode node) {
        node.setLastUpdated(LocalDateTime.now());
        return nodeRepository.save(node);
    }

    // ✅ 2. HEARTBEAT from Python agent (auto updates system metrics)
    @PostMapping("/heartbeat")
    public ComputerNode heartbeat(@RequestBody ComputerNode incoming) {

        ComputerNode node = nodeRepository.findById(incoming.getNodeId())
                .orElse(new ComputerNode());

        // ✅ Preserve existing phone number if not sent
        if (node.getPhoneNumber() != null && incoming.getPhoneNumber() == null) {
            incoming.setPhoneNumber(node.getPhoneNumber());
        }

        // ✅ Copy/update fields
        node.setNodeId(incoming.getNodeId());
        node.setIpAddress(incoming.getIpAddress());
        node.setEmployeeName(incoming.getEmployeeName());
        node.setCpuUsage(incoming.getCpuUsage());
        node.setMemoryUsage(incoming.getMemoryUsage());
        node.setAvailableMemoryGB(incoming.getAvailableMemoryGB());
        node.setActiveTasks(incoming.getActiveTasks());
        node.setEmployeePresent(incoming.isEmployeePresent());
        node.setEmployeeActive(incoming.isEmployeeActive());
        node.setCurrentActivity(incoming.getCurrentActivity());
        node.setLastUpdated(LocalDateTime.now());

        // ✅ Preserve or update phone
        if (incoming.getPhoneNumber() != null) {
    node.setPhoneNumber(incoming.getPhoneNumber());
}

        // ✅ Status logic
        if (node.getCpuUsage() > 80 || node.getMemoryUsage() > 80) {
            node.setStatus("OVERLOADED");
        } else if (node.getCpuUsage() < 30) {
            node.setStatus("IDLE");
        } else {
            node.setStatus("ONLINE");
        }

        return nodeRepository.save(node);
    }

    // ✅ 3. GET all nodes
    @GetMapping
    public List<ComputerNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    // ✅ 4. GET single node
    @GetMapping("/{id}")
    public ComputerNode getNode(@PathVariable String id) {
        return nodeRepository.findById(id).orElse(null);
    }

    // ✅ 5. DELETE node (optional)
    @DeleteMapping("/{id}")
    public String deleteNode(@PathVariable String id) {
        nodeRepository.deleteById(id);
        return "Node deleted: " + id;
    }
}