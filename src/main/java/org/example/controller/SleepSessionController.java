package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.model.SleepSession;
import org.example.service.SleepSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Сессии сна", description = "Операции с сессиями сна")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SleepSessionController {
    
    private final SleepSessionService sleepSessionService;
    
    @Operation(summary = "Получить все сессии сна")
    @GetMapping
    public ResponseEntity<List<SleepSession>> getAllSessions() {
        return ResponseEntity.ok(sleepSessionService.getAllSessions());
    }
    
    @Operation(summary = "Получить сессию по {id}")
    @GetMapping("/{id}")
    public ResponseEntity<SleepSession> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sleepSessionService.getSessionById(id));
    }
    
    @Operation(summary = "Создать новую сессию")
    @PostMapping("/{userId}")
    public ResponseEntity<SleepSession> createSession(@PathVariable Long userId,
                                                      @RequestBody SleepSession session) {
        return ResponseEntity.ok(sleepSessionService.createSession(userId, session));
    }
    
    @Operation(summary = "Удалить сессию")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sleepSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить сессию по {id} пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SleepSession>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sleepSessionService.getUserSessions(userId));
    }
}