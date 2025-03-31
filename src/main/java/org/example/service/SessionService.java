package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.cache.SessionCache;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Session;
import org.example.model.User;
import org.example.repository.SessionRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionCache sessionCache;
    
    public List<Session> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        for (Session session : sessions) {
            if (sessionCache.get(session.getId()) == null) {
                sessionCache.put(session.getId(), session);
            }
        }
        return sessions;
    }
    
    public Session getSessionById(Long id) {
        Session cachedSession = sessionCache.get(id);
        if (cachedSession != null) {
            return cachedSession;
        }
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sleep session not found"));
        sessionCache.put(id, session);
        return session;
    }
    
    public List<Session> getUserSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Session> sessions = sessionRepository.findByUser(user);
        
        for (Session session : sessions) {
            if (sessionCache.get(session.getId()) == null) {
                sessionCache.put(session.getId(), session);
            }
        }
        
        return sessions;
    }
    
    @Transactional
    public Session createSession(Long userId, Session session) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        session.setUser(user);
        
        Session savedSession = sessionRepository.save(session);
        
        sessionCache.put(savedSession.getId(), savedSession);
        
        return savedSession;
    }
    
    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sleep session not found"));
        
        sessionCache.remove(id);
        
        sessionRepository.deleteById(id);
    }
    
    public List<Session> findUserSessionsFromToday(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        
        List<Session> sessions = sessionRepository.findUserSessionsFromToday(userId);
        
        for (Session session : sessions) {
            if (sessionCache.get(session.getId()) == null) {
                sessionCache.put(session.getId(), session);
            }
        }
        
        return sessions;
    }
}