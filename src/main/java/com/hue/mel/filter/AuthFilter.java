package com.hue.mel.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1) // Runs FIRST before checking traffic limits
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // BUNDLE PROTECTION: Allow the frontend UI web assets to bypass authentication checks
        String path = httpRequest.getRequestURI();
        if (path.equals("/") || path.equals("/index.html") || path.endsWith(".js") || path.endsWith(".css")) {
            chain.doFilter(request, response);
            return;
        }

        // 1. Read the custom security header string sent from the frontend UI
        String memberToken = httpRequest.getHeader("X-Member-Token");

        // 2. Evaluate identity presence
        if (memberToken == null || memberToken.trim().isEmpty()) {
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value()); // Return HTTP 401
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"error\": \"Unauthorized\", \"message\": \"Missing spiritual portal authorization token. Please provide your Member ID.\"}"
            );
            return; // Terminate network request execution pipeline immediately
        }

        // 3. Extract the clean User ID identifier (e.g., "HUE-MEMBER-777")
        String extractedUserId = memberToken.trim();

        // 4. Inject this verified identity memory block downstream into the request environment context
        httpRequest.setAttribute("X-User-Id", extractedUserId);

        // 5. Hand execution over smoothly to the RateLimitFilter checkpoint
        chain.doFilter(request, response);
    }
}
