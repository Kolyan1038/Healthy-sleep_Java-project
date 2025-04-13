package org.example.service;

import org.example.cache.SessionCache;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Session;
import org.example.model.User;
import org.example.repository.SessionRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    
    @Mock
    private SessionRepository sessionRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SessionCache sessionCache;
    
    @InjectMocks
    private SessionService sessionService;
    
    private User testUser;
    private Session testSession;
    private Session testSession2;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        testSession = new Session();
        testSession.setId(1L);
        testSession.setUser(testUser);
        testSession.setStartTime(LocalDateTime.now().minusHours(2));
        testSession.setEndTime(LocalDateTime.now().minusHours(1));
        
        testSession2 = new Session();
        testSession2.setId(2L);
        testSession2.setUser(testUser);
        testSession2.setStartTime(LocalDateTime.now().minusHours(5));
        testSession2.setEndTime(LocalDateTime.now().minusHours(4));
    }
    
    @Test
    void getAllSessions_ShouldReturnAllSessionsAndCacheThem() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(testSession, testSession2));
        when(sessionCache.get(1L)).thenReturn(null);
        when(sessionCache.get(2L)).thenReturn(testSession2); // simulate one already in cache
        
        // Act
        List<Session> result = sessionService.getAllSessions();
        
        // Assert
        assertEquals(2, result.size());
        verify(sessionCache).put(1L, testSession); // only put the one not in cache
        verify(sessionCache, never()).put(2L, testSession2); // already in cache
    }
    
    @Test
    void getAllSessions_EmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Session> result = sessionService.getAllSessions();
        
        // Assert
        assertTrue(result.isEmpty());
        verify(sessionCache, never()).put(anyLong(), any());
    }
    
    @Test
    void getSessionById_WhenInCache_ShouldReturnCachedSession() {
        // Arrange
        when(sessionCache.get(1L)).thenReturn(testSession);
        
        // Act
        Session result = sessionService.getSessionById(1L);
        
        // Assert
        assertEquals(testSession, result);
        verify(sessionRepository, never()).findById(anyLong());
    }
    
    @Test
    void getSessionById_WhenNotInCache_ShouldFetchFromRepositoryAndCache() {
        // Arrange
        when(sessionCache.get(1L)).thenReturn(null);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        
        // Act
        Session result = sessionService.getSessionById(1L);
        
        // Assert
        assertEquals(testSession, result);
        verify(sessionCache).put(1L, testSession);
    }
    
    @Test
    void getSessionById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(sessionCache.get(1L)).thenReturn(null);
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sessionService.getSessionById(1L));
        verify(sessionCache, never()).put(anyLong(), any());
    }
    
    @Test
    void getUserSessions_UserExists_ShouldReturnSessionsAndCacheThem() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUser(testUser)).thenReturn(Arrays.asList(testSession, testSession2));
        when(sessionCache.get(1L)).thenReturn(null);
        when(sessionCache.get(2L)).thenReturn(null);
        
        // Act
        List<Session> result = sessionService.getUserSessions(1L);
        
        // Assert
        assertEquals(2, result.size());
        verify(sessionCache).put(1L, testSession);
        verify(sessionCache).put(2L, testSession2);
    }
    
    @Test
    void getUserSessions_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sessionService.getUserSessions(1L));
        verify(sessionRepository, never()).findByUser(any());
    }
    
    @Test
    void getUserSessions_NoSessions_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findByUser(testUser)).thenReturn(Collections.emptyList());
        
        // Act
        List<Session> result = sessionService.getUserSessions(1L);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(sessionCache, never()).put(anyLong(), any());
    }
    
    @Test
    void createSession_UserExists_ShouldSaveAndCacheSession() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.save(testSession)).thenReturn(testSession);
        
        // Act
        Session result = sessionService.createSession(1L, testSession);
        
        // Assert
        assertEquals(testSession, result);
        assertEquals(testUser, testSession.getUser());
        verify(sessionCache).put(1L, testSession);
    }
    
    @Test
    void createSession_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sessionService.createSession(1L, testSession));
        verify(sessionRepository, never()).save(any());
        verify(sessionCache, never()).put(anyLong(), any());
    }
    
    @Test
    void deleteSession_ShouldRemoveFromCacheAndRepository() {
        // Arrange - no setup needed for void methods
        
        // Act
        sessionService.deleteSession(1L);
        
        // Assert
        verify(sessionCache).remove(1L);
        verify(sessionRepository).deleteById(1L);
    }
    
    @Test
    void findUserSessionsFromToday_UserExists_ShouldReturnSessionsAndCacheThem() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.findUserSessionsFromToday(1L)).thenReturn(Arrays.asList(testSession));
        when(sessionCache.get(1L)).thenReturn(null);
        
        // Act
        List<Session> result = sessionService.findUserSessionsFromToday(1L);
        
        // Assert
        assertEquals(1, result.size());
        verify(sessionCache).put(1L, testSession);
    }
    
    @Test
    void findUserSessionsFromToday_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sessionService.findUserSessionsFromToday(1L));
        verify(sessionRepository, never()).findUserSessionsFromToday(anyLong());
    }
    
    @Test
    void findUserSessionsFromToday_NoSessions_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.findUserSessionsFromToday(1L)).thenReturn(Collections.emptyList());
        
        // Act
        List<Session> result = sessionService.findUserSessionsFromToday(1L);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(sessionCache, never()).put(anyLong(), any());
    }
}