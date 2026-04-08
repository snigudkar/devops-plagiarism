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
import java.util.List;
import java.util.Map;

@Document(collection = "similarity_results")
@Data
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "job_submissions_idx", 
                   def = "{'job_id': 1, 'submission1_id': 1, 'submission2_id': 1}", 
                   unique = true)
})
public class SimilarityResult {
    
    @Id
    private String id;
    
    @Indexed
    @Field("job_id")
    private String jobId;
    
    @Field("submission1_id")
    private String submission1Id;
    
    @Field("submission2_id")
    private String submission2Id;
    
    @Field("submission1_name")
    private String submission1Name;
    
    @Field("submission2_name")
    private String submission2Name;
    
    @Field("similarity_percentage")
    private Double similarityPercentage;
    
    @Field("longest_match")
    private Integer longestMatch;
    
    @Field("total_matches")
    private Integer totalMatches;
    
    @Field("matched_lines")
    private List<MatchSegment> matchedLines;
    
    @Field("flagged")
    private Boolean flagged = false;
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    @Data
    public static class MatchSegment {
        private Integer start1;
        private Integer end1;
        private Integer start2;
        private Integer end2;
        private Integer length;
        private String tokens;
    }
}