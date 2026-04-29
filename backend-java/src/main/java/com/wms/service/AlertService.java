// service/AlertService.java
package com.wms.service;

import com.wms.model.EmployeeActivity;
import com.wms.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    @Autowired private ActivityRepository activityRepository;

    public void recordAndWarn(String nodeId, String employeeName, 
                              String activityType, String description) {
        EmployeeActivity activity = new EmployeeActivity();
        activity.setNodeId(nodeId);
        activity.setEmployeeName(employeeName);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setTimestamp(LocalDateTime.now());
        activity.setWarningSent(true);
        
        activityRepository.save(activity);
        
        sendWarning(nodeId, activityType);
    }

    private void sendWarning(String nodeId, String activityType) {
        System.out.println("🚨 WARNING sent to " + nodeId 
                         + " for activity: " + activityType);
        // TODO: Push notification via WebSocket to client agent
        // TODO: Email manager
    }
}