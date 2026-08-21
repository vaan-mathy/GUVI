package com.hue.mel.repository;

import com.hue.mel.model.GatewayLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayLogRepository extends MongoRepository<GatewayLog, String> {
    // This allows your gateway to stream audit data directly into MongoDB Atlas
}
