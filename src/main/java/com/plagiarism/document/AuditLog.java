package com.plagiarism.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {
    
    @Id
    private String id;
    
    @Indexed
    @Field("user_id")
    private String userId;
    
    @Field("user_email")
    private String userEmail;
    
    @Indexed
    @Field("action")
    private String action;
    
    @Field("entity_type")
    private String entityType;
    
    @Field("entity_id")
    private String entityId;
    
    @Field("details")
    private Map<String, Object> details;
    
    @Field("ip_address")
    private String ipAddress;
    
    @Field("user_agent")
    private String userAgent;
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("status")
    private String status = "SUCCESS";
    
    @Field("error_message")
    private String errorMessage;
}