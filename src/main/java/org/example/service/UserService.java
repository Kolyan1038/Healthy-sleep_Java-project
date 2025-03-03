package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.model.SleepAdvice;
import org.example.model.User;
import org.example.repository.SleepAdviceRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final SleepAdviceRepository sleepAdviceRepository;
    
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new EntityNotFoundException("Пользователь не найден");
                });
    }
    
    public User createUser(User user) {
        log.info("Создание нового пользователя: {}", user.getUsername());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Попытка создать пользователя с уже существующим email: {}", user.getEmail());
            throw new IllegalArgumentException("Email уже используется");
        }
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID {}", id);
        userRepository.deleteById(id);
    }
    
    @Transactional
    public User updateUser(Long id, User user) {
        log.info("Обновление пользователя с ID {}", id);
        
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (user.getUsername() != null && !user.getUsername().isBlank()) {
                        log.info("Обновляем username: {} -> {}", existingUser.getUsername(), user.getUsername());
                        existingUser.setUsername(user.getUsername());
                    }
                    
                    if (user.getEmail() != null && !user.getEmail().isBlank()) {
                        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
                            log.warn("Попытка обновления email на уже существующий: {}", user.getEmail());
                            throw new IllegalArgumentException("Email уже используется");
                        }
                        log.info("Обновляем email: {} -> {}", existingUser.getEmail(), user.getEmail());
                        existingUser.setEmail(user.getEmail());
                    }
                    
                    if (user.getSleepAdvices() != null && !user.getSleepAdvices().isEmpty()) {
                        log.info("Обновление списка советов по сну у пользователя ID {}", id);
                        existingUser.getSleepAdvices().clear();
                        
                        for (SleepAdvice advice : user.getSleepAdvices()) {
                            SleepAdvice existingAdvice = sleepAdviceRepository.findById(advice.getId())
                                    .orElseThrow(() -> {
                                        log.warn("Совет по сну с ID {} не найден", advice.getId());
                                        return new EntityNotFoundException("Совет по сну не найден");
                                    });
                            existingUser.getSleepAdvices().add(existingAdvice);
                        }
                    }
                    
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> {
                    log.warn("Не удалось обновить пользователя, ID {} не найден", id);
                    return new EntityNotFoundException("Пользователь не найден");
                });
    }
    
}