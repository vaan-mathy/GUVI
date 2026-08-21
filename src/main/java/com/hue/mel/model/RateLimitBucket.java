package com.hue.mel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Document(collection = "rate_limits")
public class RateLimitBucket {

    @Id
    private String id; 

    @Field(name = "tokens")
    private double tokens; 

    @Field(name = "last_refilled")
    private Instant lastRefilled; 

    @Indexed(expireAfter = "60s")
    @Field(name = "ttl_index")
    private Instant ttlIndex; 

    // Explicit Default Constructor (Mandatory for MongoDB Mapping)
    public RateLimitBucket() {}

    // Explicit Argument Constructor
    public RateLimitBucket(String id, double tokens, Instant lastRefilled) {
        this.id = id;
        this.tokens = tokens;
        this.lastRefilled = lastRefilled;
        this.ttlIndex = lastRefilled; 
    }

    // Explicit, Failsafe Getters and Setters
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }

    public double getTokens() { 
        return tokens; 
    }
    
    public void setTokens(double tokens) { 
        this.tokens = tokens; 
    }

    public Instant getLastRefilled() { 
        return lastRefilled; 
    }
    
    public void setLastRefilled(Instant lastRefilled) { 
        this.lastRefilled = lastRefilled; 
        this.ttlIndex = lastRefilled; // Wakes up the database activity timeline tracker
    }

    public Instant getTtlIndex() {
        return ttlIndex;
    }

    public void setTtlIndex(Instant ttlIndex) {
        this.ttlIndex = ttlIndex;
    }
}
