package com.plagiarism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableAsync
@EnableMongoAuditing
public class PlagiarismApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlagiarismApplication.class, args);
    }
}