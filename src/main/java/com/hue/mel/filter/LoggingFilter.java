package com.hue.mel.filter;

import com.hue.mel.model.GatewayLog;
import com.hue.mel.repository.GatewayLogRepository;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(0) // Runs FIRST on the way in, and LAST on the way out to capture the final HTTP Status
public class LoggingFilter implements Filter {

    private final GatewayLogRepository logRepository;

    public LoggingFilter(GatewayLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Let the request travel downstream through AuthFilter, RateLimitFilter, and ServiceController
            chain.doFilter(request, response);
        } finally {
            // This block ALWAYS runs, even if errors occur downstream
            
            // Extract the user data that was injected by the AuthFilter
            String userId = (String) httpRequest.getAttribute("X-User-Id");
            
            // Check if the RateLimitFilter flagged this request as a system violation
            Boolean violated = (Boolean) httpRequest.getAttribute("X-RateLimit-Violated");

            // Assemble the telemetry metrics log package
            GatewayLog logEntry = new GatewayLog(
                httpRequest.getRemoteAddr(),                           // Client IP
                userId != null ? userId : "ANONYMOUS",                 // User Identification
                httpRequest.getRequestURI(),                           // Endpoint Accessed
                httpResponse.getStatus(),                              // Returned Network Status
                violated != null && violated                           // Rate Limit Violated Flag
            );

            // Hand the log record over to the background thread pool
            saveLogAsync(logEntry);
        }
    }

    /**
     * The @Async annotation tells Spring to execute this specific database write
     * on a completely separate thread from its internal background worker pool.
     */
    @Async
    protected void saveLogAsync(GatewayLog logEntry) {
        logRepository.save(logEntry); // Streams directly to your MongoDB Atlas Cluster
    }
}
