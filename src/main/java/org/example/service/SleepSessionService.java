package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.SleepSession;
import org.example.model.User;
import org.example.repository.SleepSessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SleepSessionService {
    
    private final SleepSessionRepository sleepSessionRepository;
    private final UserRepository userRepository;
    
    public List<SleepSession> getAllSessions() {
        return sleepSessionRepository.findAll();
    }
    
    public SleepSession getSessionById(Long id) {
        return sleepSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сеанс сна не найден"));
    }
    
    @Transactional
    public SleepSession createSession(Long userId, SleepSession session) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        
        session.setUser(user);
        return sleepSessionRepository.save(session);
    }
    
    public void deleteSession(Long id) {
        sleepSessionRepository.deleteById(id);
    }
    
    public List<SleepSession> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return sleepSessionRepository.findByUser(user);
    }
}