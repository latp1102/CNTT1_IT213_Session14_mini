package com.example.ni.service;

import com.example.ni.entity.Delivery;
import com.example.ni.entity.Incident;
import com.example.ni.repository.DeliveryRepository;
import com.example.ni.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {
    
    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;
    
    public record CreateIncidentRequest(
            String trackingCode,
            Incident.IncidentType incidentType,
            String hubCode,
            Incident.Severity severity,
            String description
    ) {}
    
    public record CreateIncidentResponse(
            boolean success,
            String message,
            Long incidentId
    ) {}
    
    public record UpdateDeliveryStatusRequest(
            String trackingCode,
            Delivery.DeliveryStatus newStatus
    ) {}
    
    public record UpdateDeliveryStatusResponse(
            boolean success,
            String message
    ) {}
    
    public Function<CreateIncidentRequest, CreateIncidentResponse> createIncidentTool() {
        return request -> {
            try {
                log.info("Creating incident for tracking code: {}", request.trackingCode);
                
                Delivery delivery = deliveryRepository.findByTrackingCode(request.trackingCode)
                        .orElse(null);
                
                if (delivery == null) {
                    log.warn("Delivery not found for tracking code: {}", request.trackingCode);
                    return new CreateIncidentResponse(
                            false,
                            "Không tìm thấy mã vận đơn: " + request.trackingCode,
                            null
                    );
                }
                
                Incident incident = Incident.builder()
                        .trackingCode(request.trackingCode)
                        .incidentType(request.incidentType)
                        .hubCode(request.hubCode != null ? request.hubCode : delivery.getHubCode())
                        .severity(request.severity)
                        .description(request.description)
                        .status(Incident.IncidentStatus.OPEN)
                        .build();
                
                Incident savedIncident = incidentRepository.save(incident);
                log.info("Incident created successfully with ID: {}", savedIncident.getId());
                
                return new CreateIncidentResponse(
                        true,
                        "Đã tạo phiếu sự cố thành công. Mã phiếu: " + savedIncident.getId(),
                        savedIncident.getId()
                );
                
            } catch (Exception e) {
                log.error("Error creating incident: {}", e.getMessage(), e);
                return new CreateIncidentResponse(
                        false,
                        "Lỗi khi tạo phiếu sự cố: " + e.getMessage(),
                        null
                );
            }
        };
    }
    
    public Function<UpdateDeliveryStatusRequest, UpdateDeliveryStatusResponse> updateDeliveryStatusTool() {
        return request -> {
            try {
                log.info("Updating delivery status for tracking code: {} to {}", 
                        request.trackingCode, request.newStatus);
                
                Delivery delivery = deliveryRepository.findByTrackingCode(request.trackingCode)
                        .orElse(null);
                
                if (delivery == null) {
                    log.warn("Delivery not found for tracking code: {}", request.trackingCode);
                    return new UpdateDeliveryStatusResponse(
                            false,
                            "Không tìm thấy mã vận đơn: " + request.trackingCode
                    );
                }
                
                delivery.setStatus(request.newStatus);
                deliveryRepository.save(delivery);
                
                log.info("Delivery status updated successfully");
                return new UpdateDeliveryStatusResponse(
                        true,
                        "Đã cập nhật trạng thái đơn hàng thành công"
                );
                
            } catch (Exception e) {
                log.error("Error updating delivery status: {}", e.getMessage(), e);
                return new UpdateDeliveryStatusResponse(
                        false,
                        "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage()
                );
            }
        };
    }
}
