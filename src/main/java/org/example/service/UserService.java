package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.cache.UserCache;
import org.example.exception.NotFoundException;
import org.example.model.Advice;
import org.example.model.Session;
import org.example.model.User;
import org.example.repository.AdviceRepository;
import org.example.repository.SessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final AdviceRepository adviceRepository;
    private final SessionRepository sessionRepository;
    private final UserCache userCache;
    
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (userCache.get(user.getId()) == null) {
                userCache.put(user.getId(), user);
            }
        }
        return users;
    }
    
    public User getUserById(Long id) {
        User cachedUser = userCache.get(id);
        if (cachedUser != null) {
            return cachedUser;
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userCache.put(id, user);
        return user;
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User savedUser = userRepository.save(user);
        userCache.put(savedUser.getId(), savedUser);
        return savedUser;
    }
    
    public void deleteUser(Long id) {
        userCache.remove(id);
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
                    User updatedUser = userRepository.save(existingUser);
                    userCache.put(id, updatedUser);
                    return updatedUser;
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
            for (Advice advice : newUser.getSleepAdvices()) {
                Advice existingAdvice = adviceRepository.findById(advice.getId())
                        .orElseThrow(() -> new NotFoundException("Совет по сну не найден"));
                existingUser.getSleepAdvices().add(existingAdvice);
            }
        }
    }
    
    private void updateSleepSessions(User existingUser, User newUser) {
        if (newUser.getSleepSessions() != null && !newUser.getSleepSessions().isEmpty()) {
            existingUser.getSleepSessions().clear();
            for (Session session : newUser.getSleepSessions()) {
                Session existingSession = sessionRepository.findById(session.getId())
                        .orElseThrow(() -> new NotFoundException("Cессия по сну не найдена"));
                existingUser.getSleepSessions().add(existingSession);
            }
        }
    }
}