package org.healthysleep.service;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.healthysleep.cache.UserCache;
import org.healthysleep.exception.DuplicateEmailException;
import org.healthysleep.exception.ResourceNotFoundException;
import org.healthysleep.model.Advice;
import org.healthysleep.model.Session;
import org.healthysleep.model.User;
import org.healthysleep.repository.AdviceRepository;
import org.healthysleep.repository.SessionRepository;
import org.healthysleep.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final AdviceRepository adviceRepository;
    private final SessionRepository sessionRepository;
    private final UserCache userCache;
    
    public UserService(UserRepository userRepository, AdviceRepository adviceRepository,
                       SessionRepository sessionRepository, UserCache userCache) {
        this.userRepository = userRepository;
        this.adviceRepository = adviceRepository;
        this.sessionRepository = sessionRepository;
        this.userCache = userCache;
    }
    
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userCache.put(id, user);
        return user;
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email is already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateEmailException("Username is already in use");
        }
        user.setRole("USER");
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
                    updateRole(existingUser, user);
                    updateSleepAdvices(existingUser, user);
                    updateSleepSessions(existingUser, user);
                    User updatedUser = userRepository.save(existingUser);
                    userCache.put(id, updatedUser);
                    return updatedUser;
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public void updateUsername(User existingUser, User newUser) {
        if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
            existingUser.setUsername(newUser.getUsername());
        }
    }
    
    public void updateEmail(User existingUser, User newUser) {
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            if (!existingUser.getEmail().equals(newUser.getEmail())
                    && userRepository.existsByEmail(newUser.getEmail())) {
                throw new DuplicateEmailException("Email is already in use");
            }
            existingUser.setEmail(newUser.getEmail());
        }
    }
    
    public void updateRole(User existingUser, User newUser) {
        if (newUser.getRole() != null && !newUser.getRole().isBlank()) {
            existingUser.setRole(newUser.getRole());
        }
    }
    
    public void updateSleepAdvices(User existingUser, User newUser) {
        if (newUser.getSleepAdvices() != null && !newUser.getSleepAdvices().isEmpty()) {
            existingUser.getSleepAdvices().clear();
            for (Advice advice : newUser.getSleepAdvices()) {
                Advice existingAdvice = adviceRepository.findById(advice.getId()).orElseThrow(()
                        -> new ResourceNotFoundException("Sleep advice is not found."));
                existingUser.getSleepAdvices().add(existingAdvice);
            }
        }
    }
    
    public void updateSleepSessions(User existingUser, User newUser) {
        if (newUser.getSleepSessions() != null && !newUser.getSleepSessions().isEmpty()) {
            existingUser.getSleepSessions().clear();
            for (Session session : newUser.getSleepSessions()) {
                Session existingSession = sessionRepository.findById(session.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Sleep session not found"));
                existingUser.getSleepSessions().add(existingSession);
            }
        }
    }

    public List<User> getUsersWithoutSessions() {
        List<User> users = userRepository.findUsersWithoutSessions();
        for (User user : users) {
            if (userCache.get(user.getId()) == null) {
                userCache.put(user.getId(), user);
            }
        }
        return users;
    }

    public List<User> getUsersWithoutAdvices() {
        List<User> users = userRepository.findUsersWithoutAdvices();
        for (User user : users) {
            if (userCache.get(user.getId()) == null) {
                userCache.put(user.getId(), user);
            }
        }
        return users;
    }
    
    public User addAdvicesToUser(Long userId, List<Long> adviceIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Advice> advices = adviceRepository.findAllById(adviceIds);
        
        if (advices.size() != adviceIds.size()) {
            throw new ResourceNotFoundException("Some advices not found");
        }
        
        user.getSleepAdvices().addAll(advices);
        userCache.put(userId, user);
        return userRepository.save(user);
    }
    
    @Transactional
    public void removeAdviceFromUser(Long userId, Long adviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));
        
        Advice advice = adviceRepository.findById(adviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Advice not found with id: " + adviceId));
        
        if (!user.getSleepAdvices().contains(advice)) {
            throw new IllegalArgumentException("User does not have this advice");
        }
        user.getSleepAdvices().remove(advice);
        userCache.put(userId, user);
        userRepository.save(user);
    }
}