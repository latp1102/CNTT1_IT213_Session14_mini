package com.example.ni.controller;

import com.example.ni.entity.Incident;
import com.example.ni.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
@Slf4j
public class IncidentController {
    
    private final IncidentRepository incidentRepository;
    
    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        try {
            Incident saved = incidentRepository.save(incident);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating incident: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() {
        return ResponseEntity.ok(incidentRepository.findAll());
    }
    
    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<List<Incident>> getIncidentsByTrackingCode(@PathVariable String trackingCode) {
        return ResponseEntity.ok(incidentRepository.findByTrackingCode(trackingCode));
    }
    
    @GetMapping("/hub/{hubCode}")
    public ResponseEntity<List<Incident>> getIncidentsByHub(@PathVariable String hubCode) {
        return ResponseEntity.ok(incidentRepository.findByHubCode(hubCode));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Incident>> getIncidentsByStatus(@PathVariable Incident.IncidentStatus status) {
        return ResponseEntity.ok(incidentRepository.findByStatus(status));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateIncidentStatus(
            @PathVariable Long id,
            @RequestParam Incident.IncidentStatus status) {
        return incidentRepository.findById(id)
                .map(incident -> {
                    incident.setStatus(status);
                    return ResponseEntity.ok(incidentRepository.save(incident));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
