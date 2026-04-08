package com.plagiarism.repository;

import com.plagiarism.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    @Query("{ 'email': ?0, 'enabled': true }")
    Optional<User> findActiveUserByEmail(String email);
    
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'last_login': ?1 } }")
    void updateLastLogin(String userId, LocalDateTime lastLogin);
    
    @Query(value = "{ 'role': ?0 }", count = true)
    long countByRole(String role);
    
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    java.util.List<User> searchByEmailPattern(String pattern);
}