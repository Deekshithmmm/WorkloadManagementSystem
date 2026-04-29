// service/WorkloadAnalyzerService.java
package com.wms.service;

import com.wms.model.ComputerNode;
import com.wms.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class WorkloadAnalyzerService {

    @Autowired private NodeRepository nodeRepository;
    @Autowired private TaskTransferService taskTransferService;

    @Value("${workload.cpu.threshold}")
    private double cpuThreshold;

    @Value("${workload.memory.threshold}")
    private double memoryThreshold;

    /**
     * Runs every 10 seconds to analyze workload and trigger transfers
     */
    @Scheduled(fixedRate = 10000)
    public void analyzeAndBalance() {
        List<ComputerNode> allNodes = nodeRepository.findAll();
        
        for (ComputerNode node : allNodes) {
            if (isOverloaded(node)) {
                System.out.println("⚠️ Overloaded node detected: " + node.getNodeId());
                Optional<ComputerNode> target = findBestTargetNode(allNodes, node);
                
                target.ifPresent(t -> {
                    System.out.println("✅ Transferring work from " + node.getNodeId() 
                                     + " → " + t.getNodeId());
                    taskTransferService.transferTask(node, t);
                });
            }
        }
    }

    private boolean isOverloaded(ComputerNode node) {
        return node.getCpuUsage() > cpuThreshold 
            || node.getMemoryUsage() > memoryThreshold;
    }

    /**
     * Selects the best target node:
     * - Employee must be PRESENT
     * - Employee must be ACTIVE (not distracted)
     * - Lowest CPU & most free memory
     */
    public Optional<ComputerNode> findBestTargetNode(List<ComputerNode> nodes, 
                                                     ComputerNode source) {
        return nodes.stream()
            .filter(n -> !n.getNodeId().equals(source.getNodeId()))
            .filter(ComputerNode::isEmployeePresent)
            .filter(ComputerNode::isEmployeeActive)
            .filter(n -> n.getCpuUsage() < 50)
            .filter(n -> n.getAvailableMemoryGB() > 2.0)
            .min(Comparator.comparingDouble(ComputerNode::getCpuUsage));
    }
}