package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.SleepAdvice;
import org.example.model.User;
import org.example.repository.SleepAdviceRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final SleepAdviceRepository sleepAdviceRepository;
    
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
                    if (user.getUsername() != null && !user.getUsername().isBlank()) {
                        existingUser.setUsername(user.getUsername());
                    }
                    
                    if (user.getEmail() != null && !user.getEmail().isBlank()) {
                        if (!existingUser.getEmail().equals(user.getEmail())
                                && userRepository.existsByEmail(user.getEmail())) {
                            throw new IllegalArgumentException("Email уже используется");
                        }
                        existingUser.setEmail(user.getEmail());
                    }
                    
                    if (user.getSleepAdvices() != null && !user.getSleepAdvices().isEmpty()) {
                        existingUser.getSleepAdvices().clear();
                        
                        for (SleepAdvice advice : user.getSleepAdvices()) {
                            SleepAdvice existingAdvice = sleepAdviceRepository.findById(
                                    advice.getId()).orElseThrow(() -> new NotFoundException(
                                    "Совет по сну не найден"
                            ));
                            existingUser.getSleepAdvices().add(existingAdvice);
                        }
                    }
                    
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
    
}