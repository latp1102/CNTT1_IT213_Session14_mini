package com.example.ni.controller;

import com.example.ni.service.RAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Slf4j
public class RAGController {
    
    private final RAGService ragService;
    
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentName", required = false) String documentName) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            String finalDocumentName = documentName != null ? documentName : file.getOriginalFilename();
            
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            Files.write(tempFile, file.getBytes());
            
            Resource resource = new org.springframework.core.io.PathResource(tempFile);
            ragService.ingestDocument(resource, finalDocumentName);
            
            Files.delete(tempFile);
            
            return ResponseEntity.ok("Document ingested successfully: " + finalDocumentName);
        } catch (IOException e) {
            log.error("Error ingesting document: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to ingest document: " + e.getMessage());
        }
    }
    
    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam("question") String question) {
        try {
            String answer = ragService.generateAnswerWithCitations(question, "Processing...", ragService.searchRelevantContext(question));
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            log.error("Error processing question: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing question: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearVectorStore() {
        try {
            ragService.clearVectorStore();
            return ResponseEntity.ok("Vector store cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing vector store: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to clear vector store: " + e.getMessage());
        }
    }
}
