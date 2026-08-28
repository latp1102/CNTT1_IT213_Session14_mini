package com.example.ni.repository;

import com.example.ni.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    
    List<Incident> findByTrackingCode(String trackingCode);
    
    List<Incident> findByHubCode(String hubCode);
    
    List<Incident> findByStatus(Incident.IncidentStatus status);
}
