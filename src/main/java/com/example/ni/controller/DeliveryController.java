package com.example.ni.controller;

import com.example.ni.entity.Delivery;
import com.example.ni.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {
    
    private final DeliveryRepository deliveryRepository;
    
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
        try {
            Delivery saved = deliveryRepository.save(delivery);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating delivery: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{trackingCode}")
    public ResponseEntity<Delivery> getDeliveryByTrackingCode(@PathVariable String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryRepository.findAll());
    }
    
    @GetMapping("/hub/{hubCode}")
    public ResponseEntity<List<Delivery>> getDeliveriesByHub(@PathVariable String hubCode) {
        return ResponseEntity.ok(deliveryRepository.findAll().stream()
                .filter(d -> d.getHubCode().equals(hubCode))
                .toList());
    }
}
