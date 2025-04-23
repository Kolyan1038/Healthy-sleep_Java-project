package org.healthysleep.service;

import java.util.List;
import org.healthysleep.cache.SessionCache;
import org.healthysleep.exception.ResourceNotFoundException;
import org.healthysleep.model.Session;
import org.healthysleep.model.User;
import org.healthysleep.repository.SessionRepository;
import org.healthysleep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionCache sessionCache;
    
    @Autowired // Необязательно, если есть только один конструктор
    public SessionService(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            SessionCache sessionCache
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionCache = sessionCache;
    }
    
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