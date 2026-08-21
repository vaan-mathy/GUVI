package com.hue.mel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {

    /**
     * A real core service endpoint providing spiritual resources for organization members.
     */
    @GetMapping("/meditation/session")
    public ResponseEntity<Map<String, Object>> getMeditationSession() {
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "title", "Morning Mindfulness Flow",
            "durationMinutes", 20,
            "instructor", "Acharya Achal",
            "message", "Welcome to your sacred portal space. Your request passed the Gateway successfully."
        ));
    }
}
