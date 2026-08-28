package com.example.ni.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    
    private final ChatClient.Builder chatClientBuilder;
    private final RAGService ragService;
    
    @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}")
    private String model;
    
    private static final String RAG_SYSTEM_PROMPT = """
            Bạn là trợ lý AI chuyên về quy chế vận chuyển logistics của RikkeiExpress.
            
            Nhiệm vụ của bạn:
            1. Trả lời câu hỏi của người dùng dựa trên tài liệu quy chế được cung cấp.
            2. BẮT BUỘC phải trích dẫn nguồn tài liệu cho mỗi thông tin được cung cấp.
            3. Nếu thông tin không có trong tài liệu, hãy trả lời lịch sự: "Tôi không tìm thấy thông tin này trong tài liệu quy chế hiện có."
            4. Không bịa đặt hoặc suy diễn thông tin không có trong tài liệu.
            5. Sử dụng ngôn ngữ Việt Nam, chuyên nghiệp và rõ ràng.
            
            Khi trả lời, hãy:
            - Giải thích ngắn gọn và chính xác
            - Trích dẫn số điều/khoản cụ thể nếu có
            - Cung cấp ví dụ minh họa khi cần thiết
            """;
    
    public String askQuestion(String question) {
        try {
            log.info("Processing RAG question: {}", question);
            
            List<Document> relevantDocs = ragService.searchRelevantContext(question);
            
            if (relevantDocs.isEmpty()) {
                return "Tôi không tìm thấy thông tin liên quan trong tài liệu quy chế. Vui lòng cung cấp thêm chi tiết hoặc liên hệ bộ phận hỗ trợ.";
            }
            
            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(RAG_SYSTEM_PROMPT)
                    .defaultAdvisors(new QuestionAnswerAdvisor(ragService.getVectorStore()))
                    .build();
            
            String answer = chatClient.prompt()
                    .user(question)
                    .call()
                    .content();
            
            String responseWithCitations = ragService.generateAnswerWithCitations(question, answer, relevantDocs);
            
            log.info("RAG answer generated successfully");
            return responseWithCitations;
            
        } catch (Exception e) {
            log.error("Error processing RAG question: {}", e.getMessage(), e);
            return "Xin lỗi, đã xảy ra lỗi khi xử lý câu hỏi của bạn. Vui lòng thử lại sau.";
        }
    }
}
