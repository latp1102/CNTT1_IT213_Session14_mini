package com.example.ni.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservabilityService {
    
    private final ChatClient.Builder chatClientBuilder;
    
    @Value("${langfuse.sample-rate:1.0}")
    private double sampleRate;
    
    @Value("${app.agent.max-iterations:6}")
    private int maxIterations;
    
    public void logTokenUsage(String operation, long promptTokens, long completionTokens, long totalTokens) {
        log.info("Token Usage - Operation: {}, Prompt: {}, Completion: {}, Total: {}",
                operation, promptTokens, completionTokens, totalTokens);
        
        double estimatedCost = calculateEstimatedCost(promptTokens, completionTokens);
        log.info("Estimated Cost - Operation: {}, Cost: ${}", operation, String.format("%.4f", estimatedCost));
    }
    
    public void logLatency(String operation, long durationMs) {
        log.info("Latency - Operation: {}, Duration: {}ms", operation, durationMs);
    }
    
    public void logToolExecution(String toolName, Map<String, Object> parameters, String result) {
        log.info("Tool Execution - Tool: {}, Parameters: {}, Result: {}",
                toolName, parameters, result);
    }
    
    private double calculateEstimatedCost(long promptTokens, long completionTokens) {
        double promptCostPer1k = 0.00015; // gpt-4o-mini input
        double completionCostPer1k = 0.0006; // gpt-4o-mini output
        
        return (promptTokens / 1000.0) * promptCostPer1k + 
               (completionTokens / 1000.0) * completionCostPer1k;
    }
    
    public boolean shouldSample() {
        return Math.random() < sampleRate;
    }
    
    public int getMaxIterations() {
        return maxIterations;
    }
}
