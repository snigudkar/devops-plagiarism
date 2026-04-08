package com.plagiarism.service;

import com.plagiarism.document.Job;
import com.plagiarism.document.SimilarityResult;
import com.plagiarism.document.User;
import com.plagiarism.dto.response.JobResponse;
import com.plagiarism.dto.response.SimilarityResponse;
import com.plagiarism.repository.JobRepository;
import com.plagiarism.repository.SimilarityResultRepository;
import com.plagiarism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
    
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SimilarityResultRepository similarityResultRepository;
    private final StorageService storageService;
    private final AsyncJobService asyncJobService;
    
    @Transactional
    public JobResponse createJob(MultipartFile file, String name, String description, 
                                 String language, MultipartFile baseCode) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Save file and create job
        String submissionPath = storageService.saveUploadedFile(file, user.getId());
        
        Job job = new Job();
        job.setUserId(user.getId());
        job.setName(name);
        job.setDescription(description);
        job.setLanguage(language);
        job.setStatus("PENDING");
        job.setSubmissionPath(submissionPath);
        job.setCreatedAt(LocalDateTime.now());
        
        if (baseCode != null && !baseCode.isEmpty()) {
            String baseCodePath = storageService.saveBaseCode(baseCode, job.getId());
            job.setBaseCodePath(baseCodePath);
        }
        
        Job savedJob = jobRepository.save(job);
        
        // Add job ID to user's job list
        user.getJobIds().add(savedJob.getId());
        userRepository.save(user);
        
        // Extract submissions
        storageService.extractSubmissions(savedJob);
        
        // Start async processing
        asyncJobService.processJob(savedJob);
        
        log.info("Job created successfully: {} by user: {}", savedJob.getId(), user.getEmail());
        
        return mapToResponse(savedJob);
    }
    
    public JobResponse getJob(String id) {
        Job job = findJobById(id);
        validateJobAccess(job);
        return mapToResponse(job);
    }
    
    public JobResponse getJobStatus(String id) {
        Job job = findJobById(id);
        validateJobAccess(job);
        return mapToResponse(job);
    }
    
    public Page<SimilarityResponse> getJobResults(String id, Double threshold, Pageable pageable) {
        Job job = findJobById(id);
        validateJobAccess(job);
        
        List<SimilarityResponse> results;
        if (threshold != null) {
            results = similarityResultRepository.findHighSimilarityPairs(id, threshold)
                    .stream()
                    .map(this::mapToSimilarityResponse)
                    .collect(Collectors.toList());
        } else {
            results = similarityResultRepository.findByJobId(id, pageable)
                    .stream()
                    .map(this::mapToSimilarityResponse)
                    .collect(Collectors.toList());
        }
        
        return new PageImpl<>(results, pageable, results.size());
    }
    
    public String getReportUrl(String id) {
        Job job = findJobById(id);
        validateJobAccess(job);
        
        if (job.getReportPath() != null) {
            return "/api/jobs/" + id + "/download";
        }
        return null;
    }
    
    public ResponseEntity<byte[]> downloadReport(String id) {
        try {
            Job job = findJobById(id);
            validateJobAccess(job);
            
            Path reportPath = Paths.get(job.getReportPath());
            Resource resource = new UrlResource(reportPath.toUri());
            
            if (resource.exists()) {
                byte[] content = resource.getContentAsByteArray();
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(content);
            }
        } catch (Exception e) {
            log.error("Error downloading report: {}", e.getMessage());
        }
        
        return ResponseEntity.notFound().build();
    }
    
    public Page<JobResponse> getMyJobs(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return jobRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional
    public void deleteJob(String id) {
        Job job = findJobById(id);
        validateJobAccess(job);
        
        // Delete physical files
        storageService.deleteJobFiles(job);
        
        // Delete related documents
        List<SimilarityResult> results = similarityResultRepository.findByJobId(id);
        similarityResultRepository.deleteAll(results);
        
        // Remove from user's job list
        userRepository.findById(job.getUserId()).ifPresent(user -> {
            user.getJobIds().remove(id);
            userRepository.save(user);
        });
        
        // Delete job
        jobRepository.delete(job);
        
        log.info("Job deleted successfully: {}", id);
    }
    
    @Transactional
    public void cancelJob(String id) {
        Job job = findJobById(id);
        validateJobAccess(job);
        
        if ("PENDING".equals(job.getStatus()) || "PROCESSING".equals(job.getStatus())) {
            job.setStatus("CANCELLED");
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);
            log.info("Job cancelled: {}", id);
        }
    }
    
    private Job findJobById(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
    
    private void validateJobAccess(Job job) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!job.getUserId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Access denied to this job");
        }
    }
    
    private JobResponse mapToResponse(Job job) {
        Double progress = calculateProgress(job);
        
        // Get statistics if available
        Double avgSimilarity = null;
        if (job.getStatistics() != null) {
            avgSimilarity = job.getStatistics().getAverageSimilarity();
        }
        
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .description(job.getDescription())
                .language(job.getLanguage())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .totalSubmissions(job.getTotalSubmissions())
                .reportUrl(getReportUrl(job.getId()))
                .progress(progress)
                .errorMessage(job.getErrorMessage())
                .averageSimilarity(avgSimilarity)
                .build();
    }
    
    private SimilarityResponse mapToSimilarityResponse(SimilarityResult result) {
        return SimilarityResponse.builder()
                .id(result.getId())
                .student1(result.getSubmission1Name())
                .student2(result.getSubmission2Name())
                .similarityPercentage(result.getSimilarityPercentage())
                .longestMatch(result.getLongestMatch())
                .totalMatches(result.getTotalMatches())
                .flagged(result.getFlagged())
                .createdAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : null)
                .build();
    }
    
    private Double calculateProgress(Job job) {
        switch (job.getStatus()) {
            case "PENDING":
                return 0.0;
            case "PROCESSING":
                return 50.0;
            case "COMPLETED":
                return 100.0;
            case "FAILED":
            case "CANCELLED":
                return -1.0;
            default:
                return 0.0;
        }
    }
}