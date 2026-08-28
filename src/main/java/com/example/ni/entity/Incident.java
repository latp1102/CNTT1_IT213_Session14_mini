package com.example.ni.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_code", nullable = false, length = 50)
    private String trackingCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", nullable = false, length = 50)
    private IncidentType incidentType;
    
    @Column(name = "hub_code", nullable = false, length = 20)
    private String hubCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private Severity severity;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private IncidentStatus status = IncidentStatus.OPEN;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum IncidentType {
        HỎNG_HÓC,
        GIAO_TRỄ,
        THẤT_LẠC
    }
    
    public enum Severity {
        LOW,
        MEDIUM,
        CRITICAL
    }
    
    public enum IncidentStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }
}
