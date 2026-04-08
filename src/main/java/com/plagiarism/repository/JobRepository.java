package com.plagiarism.repository;

import com.plagiarism.document.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    
    Page<Job> findByUserId(String userId, Pageable pageable);
    
    List<Job> findByStatus(String status);
    
    @Query("{ 'status': ?0, 'createdAt': { $lt: ?1 } }")
    List<Job> findStuckJobs(String status, LocalDateTime timeout);
    
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateStatus(String jobId, String status);
    
    @Query(value = "{ 'user_id': ?0, 'status': 'COMPLETED' }", count = true)
    long countCompletedJobsByUser(String userId);
    
    @Query(value = "{ 'user_id': ?0 }", fields = "{ 'total_submissions': 1 }")
    List<Job> findSubmissionCountsByUser(String userId);
    
    @Query("{ 'status': { $in: ['PENDING', 'PROCESSING'] } }")
    List<Job> findActiveJobs();
}