package com.plagiarism.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "submissions")
@Data
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "job_student_idx", def = "{'job_id': 1, 'student_name': 1}")
})
public class Submission {
    
    @Id
    private String id;
    
    @Indexed
    @Field("job_id")
    private String jobId;
    
    @Field("student_name")
    private String studentName;
    
    @Field("student_id")
    private String studentId;
    
    @Field("file_name")
    private String fileName;
    
    @Field("file_path")
    private String filePath;
    
    @Field("loc")
    private Integer loc = 0; // Lines of Code
    
    @Field("method_count")
    private Integer methodCount = 0;
    
    @Field("token_count")
    private Integer tokenCount = 0;
    
    @CreatedDate
    @Field("submitted_at")
    private LocalDateTime submittedAt;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    @Field("content_hash")
    private String contentHash;
    
    @Field("flagged")
    private Boolean flagged = false;
}