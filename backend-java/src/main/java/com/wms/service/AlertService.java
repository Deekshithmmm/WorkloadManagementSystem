package com.wms.service;

import com.wms.model.ComputerNode;
import com.wms.model.EmployeeActivity;
import com.wms.repository.ActivityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SmsAlertService smsAlertService;

    public void recordAndWarn(String nodeId, String employeeName,
                              String activityType, String description,
                              String phoneNumber) {

        System.out.println("🚀 recordAndWarn called");

        EmployeeActivity activity = new EmployeeActivity();

        activity.setNodeId(nodeId);
        activity.setEmployeeName(employeeName);
        activity.setDescription(description);
        activity.setTimestamp(LocalDateTime.now());

        // 🔥 Normalize activity
        String type = activityType.trim().toUpperCase();

        // 🔥 Map UI values
        if (type.equals("PHONE")) {
            type = "PHONE_USAGE";
        }

        activity.setActivityType(type);

        // 🔥 Alert condition
        boolean shouldWarn =
                type.equals("SLEEPING") ||
                   type.equals("PHONE") ||
                type.equals("PHONE_USAGE");

        activity.setWarningSent(shouldWarn);

        // ✅ Save activity
        activityRepository.save(activity);

        // 🔍 Debug logs
        System.out.println("TYPE = " + type);
        System.out.println("ALERT = " + shouldWarn);
        System.out.println("PHONE = " + phoneNumber);

        // 🔥 Send SMS (with full debug + error catch)
        if (shouldWarn && phoneNumber != null && !phoneNumber.isEmpty()) {

            System.out.println("🔥 Inside SMS block");

            try {
                String msg = "⚠️ ALERT: " + employeeName +
                        " is " + type +
                        " on node " + nodeId;

                System.out.println("📲 Sending SMS...");
                smsAlertService.sendAlert(phoneNumber, msg);

                System.out.println("✅ SMS method executed");

            } catch (Exception e) {
                System.out.println("❌ SMS FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ SMS NOT SENT (condition failed)");
        }

        sendWarning(nodeId, type);
    }

    // Optional CPU alert
    public void triggerAlert(ComputerNode node) {

        if (node.getPhoneNumber() == null) {
            System.out.println("⚠️ No phone number for node");
            return;
        }

        try {
            String msg = "⚠️ WMS ALERT: Node " + node.getNodeId()
                    + " CPU is at " + node.getCpuUsage() + "%"
                    + " | Status: " + node.getStatus();

            System.out.println("📲 Sending CPU alert SMS...");
            smsAlertService.sendAlert(node.getPhoneNumber(), msg);

        } catch (Exception e) {
            System.out.println("❌ CPU SMS FAILED: " + e.getMessage());
        }
    }

    private void sendWarning(String nodeId, String activityType) {
        System.out.println("🚨 WARNING sent to " + nodeId
                + " for activity: " + activityType);
    }
}