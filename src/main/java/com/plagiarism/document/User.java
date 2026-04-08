package com.plagiarism.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {
    
    @Id
    private String id;
    
    @Field("name")
    private String name;
    
    @Indexed(unique = true)
    @Field("email")
    private String email;
    
    @Field("password")
    private String password;
    
    @Field("role")
    private String role = "INSTRUCTOR";
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("last_login")
    private LocalDateTime lastLogin;
    
    @Field("enabled")
    private Boolean enabled = true;
    
    @Field("account_non_expired")
    private Boolean accountNonExpired = true;
    
    @Field("account_non_locked")
    private Boolean accountNonLocked = true;
    
    @Field("credentials_non_expired")
    private Boolean credentialsNonExpired = true;
    
    @Field("job_ids")
    private List<String> jobIds = new ArrayList<>();
    
    @Field("settings")
    private UserSettings settings;
    
    @Data
    public static class UserSettings {
        private String theme = "light";
        private Boolean emailNotifications = true;
        private Integer itemsPerPage = 20;
    }
}