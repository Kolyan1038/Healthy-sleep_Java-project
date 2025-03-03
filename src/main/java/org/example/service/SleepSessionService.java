package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.model.SleepSession;
import org.example.model.User;
import org.example.repository.SleepSessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для работы с сеансами сна.
 */
@Service
@RequiredArgsConstructor
public class SleepSessionService {
    
    private final SleepSessionRepository sleepSessionRepository;
    private final UserRepository userRepository;
    
    /**
     * Получить все сеансы сна.
     */
    public List<SleepSession> getAllSessions() {
        return sleepSessionRepository.findAll();
    }
    
    /**
     * Найти сеанс по ID.
     */
    public SleepSession getSessionById(Long id) {
        return sleepSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Сеанс сна не найден"));
    }
    
    /**
     * Создать новый сеанс сна.
     */
    @Transactional
    public SleepSession createSession(Long userId, SleepSession session) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        session.setUser(user);
        return sleepSessionRepository.save(session);
    }
    
    /**
     * Удалить сеанс сна.
     */
    public void deleteSession(Long id) {
        sleepSessionRepository.deleteById(id);
    }
    
    /**
     * Получить все сеансы пользователя.
     */
    public List<SleepSession> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return sleepSessionRepository.findByUser(user);
    }
}