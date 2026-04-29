// model/EmployeeActivity.java
package com.wms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class EmployeeActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nodeId;
    private String employeeName;
    private String activityType;    // PHONE_USAGE, SLEEPING, AWAY, DISTRACTED
    private String description;
    private LocalDateTime timestamp;
    private boolean warningSent;
}