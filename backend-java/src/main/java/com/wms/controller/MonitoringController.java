// controller/MonitoringController.java
package com.wms.controller;

import com.wms.model.EmployeeActivity;
import com.wms.repository.ActivityRepository;
import com.wms.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {

    @Autowired private AlertService alertService;
    @Autowired private ActivityRepository activityRepository;

    /** Python AI module reports detected distractions here */
    @PostMapping("/activity")
    public String reportActivity(@RequestBody Map<String, String> payload) {
        alertService.recordAndWarn(
            payload.get("nodeId"),
            payload.get("employeeName"),
            payload.get("activityType"),
            payload.get("description")
        );
        return "Alert recorded";
    }

    @GetMapping("/activities")
    public List<EmployeeActivity> getAllActivities() {
        return activityRepository.findAll();
    }
}