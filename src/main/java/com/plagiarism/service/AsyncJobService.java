package com.plagiarism.service;

import com.plagiarism.document.Job;
import com.plagiarism.engine.JPlagService;
import com.plagiarism.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncJobService {
    
    private final JobRepository jobRepository;
    private final JPlagService jPlagService;
    private final DeltaAnalysisService deltaAnalysisService;
    
    @Async
    @org.springframework.transaction.annotation.Transactional
    public void processJob(Job job) {
        log.info("Starting async processing for job: {}", job.getId());
        
        try {
            // Update status to PROCESSING
            job.setStatus("PROCESSING");
            job.setStartedAt(LocalDateTime.now());
            jobRepository.save(job);
            
            // Run JPlag analysis
            String reportPath = jPlagService.runAnalysis(job);
            job.setReportPath(reportPath);
            jobRepository.save(job);
            
            // Parse results and save to database
            jPlagService.parseAndSaveResults(job);
            
            // Perform delta analysis
            deltaAnalysisService.analyzeSubmissions(job);
            
            // Calculate statistics
            updateJobStatistics(job);
            
            // Update status to COMPLETED
            job.setStatus("COMPLETED");
            job.setCompletedAt(LocalDateTime.now());
            
            log.info("Job completed successfully: {}", job.getId());
            
        } catch (Exception e) {
            log.error("Error processing job: {}", e.getMessage(), e);
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            job.setCompletedAt(LocalDateTime.now());
        } finally {
            jobRepository.save(job);
        }
    }
    
    @Async
    @org.springframework.transaction.annotation.Transactional
    public void reprocessFailedJob(Job job) {
        log.info("Reprocessing failed job: {}", job.getId());
        processJob(job);
    }
    
    private void updateJobStatistics(Job job) {
        // This would calculate and update job statistics
        // Implementation depends on your needs
        log.debug("Updating statistics for job: {}", job.getId());
    }
}