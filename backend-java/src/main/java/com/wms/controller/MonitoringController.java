package com.wms.controller;

import com.wms.model.EmployeeActivity;
import com.wms.repository.ActivityRepository;
import com.wms.service.AlertService;
import com.wms.service.SmsAlertService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SmsAlertService smsAlertService;

    // ── MAIN API — receives activity from Python agent or UI ──────────────────
    @PostMapping("/activity")
    public String reportActivity(@RequestBody Map<String, String> payload) {

        System.out.println("🚀 /activity API called");

        String nodeId      = payload.get("nodeId");
        String phoneNumber = payload.get("phoneNumber"); // ✅ use directly from payload

        System.out.println("📱 Phone from payload: " + phoneNumber);
        System.out.println("🎯 Activity: " + payload.get("activityType"));

        alertService.recordAndWarn(
                nodeId,
                payload.get("employeeName"),
                payload.get("activityType"),
                payload.get("description"),
                phoneNumber
        );

        return "Alert recorded successfully";
    }

    // ── GET all activities — used by Alerts tab in UI ─────────────────────────
    @GetMapping("/activities")
    public List<EmployeeActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    // ── DIRECT SMS TEST — open in browser to test Twilio ─────────────────────
    @GetMapping("/force-sms")
    public String forceSMS() {
        System.out.println("🔥 Force SMS API called");
        try {
            smsAlertService.sendAlert(
                    "+919380961685",
                    "🔥 FORCE TEST SMS FROM BACKEND"
            );
            return "SMS triggered";
        } catch (Exception e) {
            System.out.println("❌ Force SMS failed: " + e.getMessage());
            return "SMS failed: " + e.getMessage();
        }
    }
}