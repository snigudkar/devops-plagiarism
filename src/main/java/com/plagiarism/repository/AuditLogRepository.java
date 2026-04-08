package com.plagiarism.repository;

import com.plagiarism.document.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    
    Page<AuditLog> findByUserId(String userId, Pageable pageable);
    
    List<AuditLog> findByAction(String action);
    
    @Query("{ 'created_at': { $gte: ?0, $lte: ?1 } }")
    List<AuditLog> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query(value = "{ 'user_id': ?0 }", count = true)
    long countByUserId(String userId);
    
    @Query("{ 'entity_type': ?0, 'entity_id': ?1 }")
    List<AuditLog> findByEntity(String entityType, String entityId);
}