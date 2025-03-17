package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.model.Session;
import org.example.service.SessionService;
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
public class SessionController {
    
    private final SessionService sessionService;
    
    @Operation(summary = "Получить все сессии сна")
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }
    
    @Operation(summary = "Получить сессию по {id}")
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }
    
    @Operation(summary = "Создать новую сессию")
    @PostMapping("/{userId}")
    public ResponseEntity<Session> createSession(@PathVariable Long userId,
                                                      @RequestBody Session session) {
        return ResponseEntity.ok(sessionService.createSession(userId, session));
    }
    
    @Operation(summary = "Удалить сессию")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить сессии по {id} пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }
    
    @Operation(summary = "Получить по {id} пользователя сессию c текущего дня и позже")
    @GetMapping("/today/{userId}")
    public ResponseEntity<List<Session>> getAllTodaySession(@PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.findUserSessionsFromToday(userId));
    }
}