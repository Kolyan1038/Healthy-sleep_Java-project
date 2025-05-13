package org.healthysleep.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.healthysleep.model.User;
import org.healthysleep.repository.UserRepository;
import org.healthysleep.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Login/Signup")
@RestController
public class AuthController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
    }
    
    @Operation(summary = "Создать нового пользователя")
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userService.createUser(user));
    }
    
    @Operation(summary = "Войти в существующий аккаунт")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        
        String token = generateToken(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put(
                "user",
                new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole())
        );
        
        return ResponseEntity.ok(response);
    }
    
    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86_400_000)) // 24 часа
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    
    public record LoginRequest(String username, String password) {}

    public record UserDto(Long id, String name, String email, String role) {}
}