package com.hue.mel.filter;

import com.hue.mel.service.RateLimitService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Enterprise gateway middleware that intercepts incoming network requests
 * and enforces user-profile or IP-based traffic restrictions.
 */
@Component
@Order(2) // Runs second, right after the AuthFilter confirms user identity
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;

    // Spring Boot automatically injects your core rate limiting math logic engine here
    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Fetch the unique User ID established earlier by the AuthFilter
        String userId = (String) httpRequest.getAttribute("X-User-Id");

        // 2. Safety Fallback: Use their physical network IP address if the request is unauthenticated
        String rateLimitKey = (userId != null) ? userId : httpRequest.getRemoteAddr();

        // 3. Evaluate the request using our Token Bucket algorithmic service
        boolean allowed = rateLimitService.isAllowed(rateLimitKey);

        if (!allowed) {
            // Flag the request context for the monitoring analytics logger system
            httpRequest.setAttribute("X-RateLimit-Violated", true);

            // 4. Block the request and return a structured professional JSON error packet
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // HTTP 429
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            String jsonErrorResponse = "{"
                + "\"error\": \"Too Many Requests\","
                + "\"message\": \"Your spiritual portal user account has exceeded its traffic allowance. Please wait a moment.\""
                + "}";
                
            httpResponse.getWriter().write(jsonErrorResponse);
            return; // Terminate execution immediately to protect backend servers
        }

        // 5. If allowed, seamlessly pass execution down to the target microservice endpoint
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional default lifecycle setup initialization if needed later
    }

    @Override
    public void destroy() {
        // Optional cleaning of operational resources during microservice shutdown
    }
}
