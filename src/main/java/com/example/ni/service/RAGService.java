package com.example.ni.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGService {
    
    private final VectorStore vectorStore;
    
    @Value("${app.rag.chunk-size:500}")
    private int chunkSize;
    
    @Value("${app.rag.chunk-overlap:100}")
    private int chunkOverlap;
    
    @Value("${app.rag.max-results:5}")
    private int maxResults;
    
    public void ingestDocument(Resource resource, String documentName) {
        try {
            log.info("Starting document ingestion for: {}", documentName);
            
            TextReader textReader = new TextReader(resource);
            textReader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
            List<Document> documents = textReader.get();
            
            TokenTextSplitter textSplitter = new TokenTextSplitter(chunkSize, chunkOverlap, 5, 10000, true);
            List<Document> splitDocuments = textSplitter.apply(documents);
            
            for (Document doc : splitDocuments) {
                Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
                metadata.put("document_name", documentName);
                metadata.put("chunk_index", metadata.getOrDefault("chunk_index", 0));
                
                Document enhancedDoc = new Document(doc.getText(), metadata);
                vectorStore.add(List.of(enhancedDoc));
            }
            
            log.info("Document ingestion completed. {} chunks created for {}", splitDocuments.size(), documentName);
        } catch (Exception e) {
            log.error("Error during document ingestion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to ingest document: " + e.getMessage(), e);
        }
    }
    
    public List<Document> searchRelevantContext(String query) {
        List<Document> results = vectorStore.similaritySearch(query);
        log.info("Found {} relevant documents for query: {}", results.size(), query);
        
        return results;
    }
    
    public String generateAnswerWithCitations(String question, String answer, List<Document> sourceDocuments) {
        StringBuilder response = new StringBuilder();
        response.append(answer);
        
        if (!sourceDocuments.isEmpty()) {
            response.append("\n\n**Nguồn tham khảo:**\n");
            for (int i = 0; i < sourceDocuments.size(); i++) {
                Document doc = sourceDocuments.get(i);
                String docName = (String) doc.getMetadata().getOrDefault("document_name", "Unknown");
                String chunkIndex = String.valueOf(doc.getMetadata().getOrDefault("chunk_index", "?"));
                response.append(String.format("- %s (Đoạn %s)\n", docName, chunkIndex));
            }
        }
        
        return response.toString();
    }
    
    public void clearVectorStore() {
        log.info("Clearing vector store...");
        vectorStore.delete(List.of());
        log.info("Vector store cleared successfully");
    }
    
    public VectorStore getVectorStore() {
        return vectorStore;
    }
}
