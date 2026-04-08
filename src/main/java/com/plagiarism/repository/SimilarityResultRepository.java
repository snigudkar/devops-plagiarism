package com.plagiarism.repository;

import com.plagiarism.document.SimilarityResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimilarityResultRepository extends MongoRepository<SimilarityResult, String> {
    
    List<SimilarityResult> findByJobId(String jobId);
    
    Page<SimilarityResult> findByJobId(String jobId, Pageable pageable);
    
    @Query(value = "{ 'job_id': ?0, 'similarity_percentage': { $gt: ?1 } }", 
           sort = "{ 'similarity_percentage': -1 }")
    List<SimilarityResult> findHighSimilarityPairs(String jobId, Double threshold);
    
    @Query("{ 'job_id': ?0, 'flagged': true }")
    List<SimilarityResult> findFlaggedResults(String jobId);
    
    @Query(value = "{ 'job_id': ?0 }", fields = "{ 'similarity_percentage': 1 }")
    List<Double> findSimilarityPercentagesByJobId(String jobId);
    
    @Query("{ '$or': [ { 'submission1_id': ?0 }, { 'submission2_id': ?0 } ] }")
    List<SimilarityResult> findBySubmissionId(String submissionId);
    
    @Query("{ 'job_id': ?0 }")
    @Update("{ '$set': { 'flagged': ?1 } }")
    void updateFlaggedByJobId(String jobId, boolean flagged);
    
    long countByJobIdAndFlaggedTrue(String jobId);
}