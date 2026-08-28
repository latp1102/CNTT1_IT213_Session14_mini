package com.example.ni.repository;

import com.example.ni.entity.VectorStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStore, Long> {
    
    List<VectorStore> findByContentContainingIgnoreCase(String content);
}
