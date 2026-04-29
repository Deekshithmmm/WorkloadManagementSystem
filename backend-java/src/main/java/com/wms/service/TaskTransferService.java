package com.wms.service;

import com.wms.model.ComputerNode;
import com.wms.model.Task;
import com.wms.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TaskTransferService {

    @Autowired
    private TaskRepository taskRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // Agent receiver runs on port 9090 on every machine
    private static final int AGENT_PORT = 9090;

    public void transferTask(ComputerNode source, ComputerNode target) {

        // ── 1. Build the task payload ──────────────────────────────────────────
        String taskName    = "Workload transferred from " + source.getNodeId();
        String taskPayload = buildPayload(source);

        // ── 2. Save transfer record to DB ─────────────────────────────────────
        Task task = new Task();
        task.setTaskName(taskName);
        task.setTaskPayload(taskPayload);
        task.setOriginNodeId(source.getNodeId());
        task.setAssignedNodeId(target.getNodeId());
        task.setStatus("PENDING");           // will be updated after dispatch
        task.setCreatedAt(LocalDateTime.now());
        task.setRequiredCpu(20.0);
        task.setRequiredMemoryGB(1.5);
        task = taskRepository.save(task);

        System.out.println("📋 Task #" + task.getId() + " created: "
                + source.getNodeId() + " → " + target.getNodeId());

        // ── 3. Dispatch to target agent via HTTP ──────────────────────────────
        boolean success = dispatchToAgent(target, task);

        // ── 4. Update status based on dispatch result ─────────────────────────
        if (success) {
            task.setStatus("TRANSFERRED");
            System.out.println("✅ Task #" + task.getId()
                    + " dispatched to " + target.getNodeId()
                    + " (" + target.getIpAddress() + ":" + AGENT_PORT + ")");
        } else {
            task.setStatus("FAILED");
            System.out.println("❌ Task #" + task.getId()
                    + " dispatch FAILED — target agent unreachable at "
                    + target.getIpAddress() + ":" + AGENT_PORT);
        }

        taskRepository.save(task);
    }

    // ── Sends POST /execute to the target machine's Python agent ──────────────
    private boolean dispatchToAgent(ComputerNode target, Task task) {
        String url = "http://" + target.getIpAddress() + ":" + AGENT_PORT + "/execute";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("taskId",      task.getId());
            body.put("taskName",    task.getTaskName());
            body.put("taskPayload", task.getTaskPayload());
            body.put("originNode",  task.getOriginNodeId());
            body.put("requiredCpu", task.getRequiredCpu());
            body.put("requiredRam", task.getRequiredMemoryGB());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            System.out.println("⚠️  Could not reach agent at " + url
                    + " — " + e.getMessage());
            return false;
        }
    }

    // ── Builds the actual shell command the target agent will run ─────────────
    // Replace these examples with real commands for your use case
    private String buildPayload(ComputerNode source) {
        // Example payloads — pick one or replace with your real task logic:

        // 1. Write a log file confirming receipt
        return "echo Task received from " + source.getNodeId()
                + " at %DATE% %TIME% >> C:\\wms_tasks\\task_log.txt";

        // 2. Run a Python processing script
        // return "python C:\\scripts\\process_data.py --source " + source.getNodeId();

        // 3. Copy files from a shared location
        // return "xcopy \\\\server\\shared\\pending C:\\local\\work /E /Y";

        // 4. Start a background service
        // return "net start MyProcessingService";
    }
}