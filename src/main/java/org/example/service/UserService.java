package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.SleepAdvice;
import org.example.model.SleepSession;
import org.example.model.User;
import org.example.repository.SleepAdviceRepository;
import org.example.repository.SleepSessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final SleepAdviceRepository sleepAdviceRepository;
    private final SleepSessionRepository sleepSessionRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUsername(existingUser, user);
                    updateEmail(existingUser, user);
                    updateSleepAdvices(existingUser, user);
                    updateSleepSessions(existingUser, user);
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
    
    private void updateUsername(User existingUser, User newUser) {
        if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
            existingUser.setUsername(newUser.getUsername());
        }
    }
    
    private void updateEmail(User existingUser, User newUser) {
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            if (!existingUser.getEmail().equals(newUser.getEmail())
                    && userRepository.existsByEmail(newUser.getEmail())) {
                throw new IllegalArgumentException("Email уже используется");
            }
            existingUser.setEmail(newUser.getEmail());
        }
    }
    
    private void updateSleepAdvices(User existingUser, User newUser) {
        if (newUser.getSleepAdvices() != null && !newUser.getSleepAdvices().isEmpty()) {
            existingUser.getSleepAdvices().clear();
            for (SleepAdvice advice : newUser.getSleepAdvices()) {
                SleepAdvice existingAdvice = sleepAdviceRepository.findById(advice.getId())
                        .orElseThrow(() -> new NotFoundException("Совет по сну не найден"));
                existingUser.getSleepAdvices().add(existingAdvice);
            }
        }
    }
    
    private void updateSleepSessions(User existingUser, User newUser) {
        if (newUser.getSleepSessions() != null && !newUser.getSleepSessions().isEmpty()) {
            existingUser.getSleepSessions().clear();
            for (SleepSession session : newUser.getSleepSessions()) {
                SleepSession existingSession = sleepSessionRepository.findById(session.getId())
                        .orElseThrow(() -> new NotFoundException("Cессия по сну не найдена"));
                existingUser.getSleepSessions().add(existingSession);
            }
        }
    }
}