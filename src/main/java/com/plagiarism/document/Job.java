package com.plagiarism.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "jobs")
@Data
@NoArgsConstructor
public class Job {
    
    @Id
    private String id;
    
    @Indexed
    @Field("user_id")
    private String userId;
    
    @Field("name")
    private String name;
    
    @Field("description")
    private String description;
    
    @Field("language")
    private String language = "java";
    
    @Field("status")
    private String status = "PENDING";
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("started_at")
    private LocalDateTime startedAt;
    
    @Field("completed_at")
    private LocalDateTime completedAt;
    
    @Field("submission_path")
    private String submissionPath;
    
    @Field("base_code_path")
    private String baseCodePath;
    
    @Field("report_path")
    private String reportPath;
    
    @Field("total_submissions")
    private Integer totalSubmissions = 0;
    
    @Field("error_message")
    private String errorMessage;
    
    @Field("metadata")
    private Map<String, Object> metadata = new HashMap<>();
    
    @Field("submission_ids")
    private List<String> submissionIds = new ArrayList<>();
    
    @Field("similarity_result_ids")
    private List<String> similarityResultIds = new ArrayList<>();
    
    @Field("statistics")
    private JobStatistics statistics;
    
    @Data
    public static class JobStatistics {
        private Double averageSimilarity;
        private Double maxSimilarity;
        private Integer flaggedCount;
        private Integer totalComparisons;
        private Long processingTimeMs;
    }
}