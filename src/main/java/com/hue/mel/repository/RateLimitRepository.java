package com.hue.mel.repository;

import com.hue.mel.model.RateLimitBucket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends MongoRepository<RateLimitBucket, String> {
    // Spring Boot automatically builds standard operations for you, such as:
    // .findById(String id), .save(RateLimitBucket bucket), and .deleteById(String id)
}
