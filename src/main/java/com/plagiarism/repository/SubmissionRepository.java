package com.plagiarism.repository;

import com.plagiarism.document.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {
    
    List<Submission> findByJobId(String jobId);
    
    Optional<Submission> findByJobIdAndStudentName(String jobId, String studentName);
    
    @Query(value = "{ 'job_id': ?0 }", sort = "{ 'loc': -1 }")
    List<Submission> findByJobIdOrderByLocDesc(String jobId);
    
    @Query(value = "{ 'job_id': ?0 }", fields = "{ 'loc': 1 }")
    List<Integer> findLocByJobId(String jobId);
    
    @Query("{ 'job_id': ?0, 'loc': { $gt: ?1 } }")
    List<Submission> findSubmissionsAboveLocThreshold(String jobId, int threshold);
    
    @Query("{ 'job_id': ?0, 'flagged': true }")
    List<Submission> findFlaggedSubmissions(String jobId);
    
    @Query("{ 'job_id': ?0 }")
    @Update("{ '$set': { 'flagged': ?1 } }")
    void updateFlaggedStatus(String jobId, boolean flagged);
    
    long countByJobId(String jobId);
}