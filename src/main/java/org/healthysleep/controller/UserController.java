package org.healthysleep.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.healthysleep.model.User;
import org.healthysleep.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Пользователи", description = "Операции с пользователями")
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @Operation(summary = "Получить пользователя по {id}")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @Operation(summary = "Создать несколько пользователей")
    @PostMapping("/bulk")
    public ResponseEntity<List<User>> createUser(@RequestBody List<User> users) {
        List<User> savedUsers = users.stream()
                .map(userService::createUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(savedUsers);
    }
    
    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }
    
    @Operation(summary = "Добавить советы пользователю")
    @PostMapping("/{userId}/advices")
    public ResponseEntity<User> addAdvicesToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> adviceIds) {
        User user = userService.addAdvicesToUser(userId, adviceIds);
        return ResponseEntity.ok(user);
    }
    
    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
    
    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Удалить совет у пользователя")
    @DeleteMapping("/{userId}/advices/{adviceId}")
    public ResponseEntity<Void> removeAdviceFromUser(
            @PathVariable Long userId,
            @PathVariable Long adviceId) {
        userService.removeAdviceFromUser(userId, adviceId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить всех пользователей без сессий")
    @GetMapping("/users-without/sessions")
    public ResponseEntity<List<User>> getUsersWithoutSessions() {
        return ResponseEntity.ok(userService.getUsersWithoutSessions());
    }

    @Operation(summary = "Получить всех пользователей без советов")
    @GetMapping("/users-without/advices")
    public ResponseEntity<List<User>> getUsersWithoutAdvices() {
        return ResponseEntity.ok(userService.getUsersWithoutAdvices());
    }
}
