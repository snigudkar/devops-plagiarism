package com.plagiarism.service;

import com.plagiarism.document.Job;
import com.plagiarism.document.Submission;
import com.plagiarism.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {
    
    @Value("${storage.upload-dir}")
    private String uploadDir;
    
    @Value("${storage.extract-dir}")
    private String extractDir;
    
    @Value("${storage.report-dir}")
    private String reportDir;
    
    private final SubmissionRepository submissionRepository;
    
    public String saveUploadedFile(MultipartFile file, String userId) {
        try {
            Path userUploadDir = Paths.get(uploadDir, userId);
            Files.createDirectories(userUploadDir);
            
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = userUploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File saved: {}", filePath);
            return filePath.toString();
            
        } catch (IOException e) {
            log.error("Error saving uploaded file: {}", e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }
    
    public String saveBaseCode(MultipartFile file, String jobId) {
        try {
            Path jobBaseCodeDir = Paths.get(extractDir, jobId, "base-code");
            Files.createDirectories(jobBaseCodeDir);
            
            String fileName = file.getOriginalFilename();
            Path filePath = jobBaseCodeDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return filePath.toString();
            
        } catch (IOException e) {
            log.error("Error saving base code: {}", e.getMessage());
            throw new RuntimeException("Failed to save base code", e);
        }
    }
    
    public void extractSubmissions(Job job) {
        Path extractPath = Paths.get(extractDir, job.getId(), "submissions");
        
        try {
            Files.createDirectories(extractPath);
            
            try (ZipFile zipFile = new ZipFile(job.getSubmissionPath())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                int submissionCount = 0;
                
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Path entryPath = extractPath.resolve(entry.getName());
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        Files.copy(zipFile.getInputStream(entry), entryPath, 
                                  StandardCopyOption.REPLACE_EXISTING);
                        
                        // Create submission record
                        createSubmissionRecord(job, entry, entryPath);
                        submissionCount++;
                    }
                }
                
                job.setTotalSubmissions(submissionCount);
                log.info("Extracted {} submissions for job: {}", submissionCount, job.getId());
            }
            
        } catch (IOException e) {
            log.error("Error extracting submissions: {}", e.getMessage());
            throw new RuntimeException("Failed to extract submissions", e);
        }
    }
    
    private void createSubmissionRecord(Job job, ZipEntry entry, Path entryPath) {
        try {
            String fileName = entry.getName();
            String studentName = extractStudentName(fileName);
            String contentHash = calculateFileHash(entryPath);
            
            Submission submission = new Submission();
            submission.setJobId(job.getId());
            submission.setStudentName(studentName);
            submission.setFileName(fileName);
            submission.setFilePath(entryPath.toString());
            submission.setContentHash(contentHash);
            
            // Basic metrics (will be updated by JPlag later)
            submission.setLoc(countLines(entryPath));
            
            Submission saved = submissionRepository.save(submission);
            
            // Add submission ID to job
            if (job.getSubmissionIds() == null) {
                job.setSubmissionIds(new java.util.ArrayList<>());
            }
            job.getSubmissionIds().add(saved.getId());
            
        } catch (Exception e) {
            log.error("Error creating submission record: {}", e.getMessage());
        }
    }
    
    private String extractStudentName(String fileName) {
        // Extract student name from filename
        String name = fileName.replaceAll("_", " ")
                .replaceAll("\\.[^.]*$", "") // Remove extension
                .replaceAll("\\d+", "") // Remove numbers
                .replaceAll("[^a-zA-Z\\s]", "") // Remove special characters
                .trim();
        
        return name.isEmpty() ? "Unknown" : name;
    }
    
    private String calculateFileHash(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(filePath);
        byte[] hashBytes = digest.digest(fileBytes);
        
        try (Formatter formatter = new Formatter()) {
            for (byte b : hashBytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
    
    private int countLines(Path filePath) throws IOException {
        return (int) Files.lines(filePath).count();
    }
    
    public void deleteJobFiles(Job job) {
        try {
            // Delete extracted submissions
            Path extractPath = Paths.get(extractDir, job.getId());
            if (Files.exists(extractPath)) {
                deleteDirectory(extractPath);
            }
            
            // Delete report files
            if (job.getReportPath() != null) {
                Path reportPath = Paths.get(job.getReportPath());
                if (Files.exists(reportPath)) {
                    deleteDirectory(reportPath);
                }
            }
            
            // Delete original upload
            Path uploadPath = Paths.get(job.getSubmissionPath());
            if (Files.exists(uploadPath)) {
                Files.deleteIfExists(uploadPath);
            }
            
            log.info("Deleted files for job: {}", job.getId());
            
        } catch (IOException e) {
            log.error("Error deleting job files: {}", e.getMessage());
        }
    }
    
    private void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}