package com.hue.mel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "gateway_logs")
@CompoundIndexes({
    // Optimises database search speeds when rendering analytical dashboard screens later
    @CompoundIndex(name = "analytics_idx", def = "{'user_id': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "security_idx", def = "{'client_ip': 1, 'timestamp': -1}")
})
public class GatewayLog {

    @Id
    private String id; // Automatically managed unique identifier for each log row

    @Field(name = "client_ip")
    private String clientIp; // The machine IP address making the incoming request

    @Field(name = "user_id")
    private String userId; // The unique identification of the member ("ANONYMOUS" if not logged in)

    @Field(name = "endpoint")
    private String endpoint; // The target web address accessed (e.g., "/api/v1/meditation")

    @Field(name = "http_status")
    private int httpStatus; // The network response code (e.g., 200 = Success, 429 = Rate Limited)

    @Field(name = "is_violated")
    private boolean isViolated; // Set explicitly to true if blocked by the rate limiter

    @Field(name = "timestamp")
    private Instant timestamp; // The precise moment this network request took place

    public GatewayLog(String clientIp, String userId, String endpoint, int httpStatus, boolean isViolated) {
        this.clientIp = clientIp;
        this.userId = userId != null ? userId : "ANONYMOUS";
        this.endpoint = endpoint;
        this.httpStatus = httpStatus;
        this.isViolated = isViolated;
        this.timestamp = Instant.now(); // Automatically tags the exact current server time
    }
}
