package com.plagiarism.engine;

import com.plagiarism.document.Job;
import com.plagiarism.document.SimilarityResult;
import com.plagiarism.document.Submission;
import com.plagiarism.repository.SimilarityResultRepository;
import com.plagiarism.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JPlagService {
    
    @Value("${jplag.jar-path}")
    private String jplagJarPath;
    
    @Value("${jplag.language}")
    private String defaultLanguage;
    
    @Value("${jplag.timeout-minutes}")
    private int timeoutMinutes;
    
    private final SubmissionRepository submissionRepository;
    private final SimilarityResultRepository similarityResultRepository;
    private final JPlagResultParser resultParser;
    
    public String runAnalysis(Job job) throws Exception {
        log.info("Running JPlag analysis for job: {}", job.getId());
        
        String submissionsPath = Paths.get(job.getSubmissionPath()).toString();
        String reportPath = Paths.get("./reports", job.getId()).toString();
        
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add(jplagJarPath);
        command.add(submissionsPath);
        command.add("-l");
        command.add(job.getLanguage() != null ? job.getLanguage() : defaultLanguage);
        command.add("-r");
        command.add(reportPath);
        
        // Add base code if exists
        if (job.getBaseCodePath() != null && !job.getBaseCodePath().isEmpty()) {
            command.add("-bc");
            command.add(job.getBaseCodePath());
        }
        
        log.debug("Executing command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Read output
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("JPlag output: {}", line);
            }
        }
        
        boolean completed = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);
        
        if (!completed) {
            process.destroyForcibly();
            throw new RuntimeException("JPlag analysis timed out after " + timeoutMinutes + " minutes");
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new RuntimeException("JPlag analysis failed with exit code: " + exitCode);
        }
        
        log.info("JPlag analysis completed. Report saved to: {}", reportPath);
        return reportPath;
    }
    
    public void parseAndSaveResults(Job job) throws Exception {
        log.info("Parsing JPlag results for job: {}", job.getId());
        
        Path reportPath = Paths.get(job.getReportPath());
        Path matchesFile = reportPath.resolve("matches.json");
        
        if (!matchesFile.toFile().exists()) {
            log.warn("No matches.json found for job: {}", job.getId());
            return;
        }
        
        List<SimilarityMatch> matches = resultParser.parseMatches(matchesFile);
        
        for (SimilarityMatch match : matches) {
            Submission sub1 = findOrCreateSubmission(job, match.getFile1());
            Submission sub2 = findOrCreateSubmission(job, match.getFile2());
            
            SimilarityResult result = new SimilarityResult();
            result.setJobId(job.getId());
            result.setSubmission1Id(sub1.getId());
            result.setSubmission2Id(sub2.getId());
            result.setSubmission1Name(sub1.getStudentName());
            result.setSubmission2Name(sub2.getStudentName());
            result.setSimilarityPercentage(match.getSimilarity());
            result.setLongestMatch(match.getLongestMatch());
            result.setTotalMatches(match.getTotalMatches());
            
            similarityResultRepository.save(result);
        }
        
        log.info("Saved {} similarity results for job: {}", matches.size(), job.getId());
    }
    
    private Submission findOrCreateSubmission(Job job, String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String studentName = extractStudentName(fileName);
        
        return submissionRepository.findByJobIdAndStudentName(job.getId(), studentName)
                .orElseGet(() -> {
                    Submission submission = new Submission();
                    submission.setJobId(job.getId());
                    submission.setStudentName(studentName);
                    submission.setFileName(fileName);
                    submission.setFilePath(filePath);
                    return submissionRepository.save(submission);
                });
    }
    
    private String extractStudentName(String fileName) {
        // Remove extension and common patterns
        return fileName.replaceAll("\\.[^.]*$", "")
                .replaceAll("_", " ")
                .replaceAll("\\d+", "")
                .trim();
    }
}