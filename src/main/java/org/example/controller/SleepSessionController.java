package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.SleepSession;
import org.example.service.SleepSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с сеансами сна.
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SleepSessionController {
    
    private final SleepSessionService sleepSessionService;
    
    @GetMapping
    public ResponseEntity<List<SleepSession>> getAllSessions() {
        return ResponseEntity.ok(sleepSessionService.getAllSessions());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SleepSession> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sleepSessionService.getSessionById(id));
    }
    
    @PostMapping("/{userId}")
    public ResponseEntity<SleepSession> createSession(@PathVariable Long userId, @RequestBody SleepSession session) {
        return ResponseEntity.ok(sleepSessionService.createSession(userId, session));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sleepSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SleepSession>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sleepSessionService.getUserSessions(userId));
    }
}