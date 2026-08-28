package com.example.ni.controller;

import com.example.ni.service.OperationsChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
@Slf4j
public class OperationsController {
    
    private final OperationsChatService operationsChatService;
    
    public record ChatRequest(String message) {}
    public record ChatResponse(String response) {}
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String response = operationsChatService.processCustomerMessage(request.message());
            return ResponseEntity.ok(new ChatResponse(response));
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse("Lỗi xử lý: " + e.getMessage()));
        }
    }
}
