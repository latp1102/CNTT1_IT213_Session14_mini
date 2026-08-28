package com.example.ni.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationsChatService {
    
    private final ChatClient.Builder chatClientBuilder;
    private final AgentService agentService;
    
    @Value("${app.agent.max-iterations:6}")
    private int maxIterations;
    
    private static final String AGENT_SYSTEM_PROMPT = """
            Bạn là trợ lý AI vận hành thông minh của RikkeiExpress, chuyên xử lý sự cố vận chuyển.
            
            Nhiệm vụ của bạn:
            1. Phân tích tin nhắn của khách hàng để xác định:
               - Mã vận đơn (trackingCode): định dạng như RK-2026-001
               - Loại sự cố (incidentType): HỎNG_HÓC, GIAO_TRỄ, THẤT_LẠC
               - Bưu cục (hubCode): mã bưu cục như HN-01, SG-02
               - Mức độ nghiêm trọng (severity): LOW, MEDIUM, CRITICAL
            
            2. Khi phát hiện sự cố, tự động gọi các công cụ:
               - createIncidentTool: Tạo phiếu sự cố mới
               - updateDeliveryStatusTool: Cập nhật trạng thái đơn hàng
            
            3. Quy tắc cập nhật trạng thái:
               - HỎNG_HÓC -> cập nhật trạng thái đơn hàng thành DAMAGED
               - GIAO_TRỄ -> cập nhật trạng thái đơn hàng thành DELAYED
               - THẤT_LẠC -> giữ nguyên trạng thái, ghi nhận sự cố
            
            4. Phản hồi với khách hàng:
               - Xác nhận đã ghi nhận sự cố
               - Cung cấp mã phiếu sự cố
               - Cam kết thời gian xử lý
               - Lịch sử sự cần thiết
            
            5. Nếu không tìm thấy mã vận đơn, thông báo lịch sự và yêu cầu kiểm tra lại.
            
            Sử dụng ngôn ngữ Việt Nam, chuyên nghiệp và thân thiện.
            """;
    
    public String processCustomerMessage(String message) {
        try {
            log.info("Processing customer message: {}", message);
            
            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(AGENT_SYSTEM_PROMPT)
                    .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                    .defaultFunctions(
                            FunctionCallback.builder()
                                    .function("createIncidentTool", agentService.createIncidentTool())
                                    .description("Tạo phiếu sự cố mới cho đơn hàng")
                                    .inputType(AgentService.CreateIncidentRequest.class)
                                    .build(),
                            FunctionCallback.builder()
                                    .function("updateDeliveryStatusTool", agentService.updateDeliveryStatusTool())
                                    .description("Cập nhật trạng thái đơn hàng")
                                    .inputType(AgentService.UpdateDeliveryStatusRequest.class)
                                    .build()
                    )
                    .build();
            
            String response = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            
            log.info("Agent response generated successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error processing customer message: {}", e.getMessage(), e);
            return "Xin lỗi, đã xảy ra lỗi khi xử lý tin nhắn của bạn. Vui lòng liên hệ tổng đài hỗ trợ.";
        }
    }
}
