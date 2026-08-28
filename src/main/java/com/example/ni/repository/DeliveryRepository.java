package com.example.ni.repository;

import com.example.ni.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    Optional<Delivery> findByTrackingCode(String trackingCode);
    
    boolean existsByTrackingCode(String trackingCode);
}
