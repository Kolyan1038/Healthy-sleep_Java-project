package org.example.service;

import org.example.cache.UserCache;
import org.example.exception.DuplicateEmailException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Advice;
import org.example.model.Session;
import org.example.model.User;
import org.example.repository.AdviceRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AdviceRepository adviceRepository;
    
    @Mock
    private SessionRepository sessionRepository;
    
    @Mock
    private UserCache userCache;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private User testUser2;
    private Advice testAdvice;
    private Session testSession;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        
        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("testUser2");
        testUser2.setEmail("test2@example.com");
        
        testAdvice = new Advice();
        testAdvice.setId(1L);
        testAdvice.setAdvice("Test advice");
        
        testSession = new Session();
        testSession.setId(1L);
        testSession.setStartTime(LocalDateTime.now());
        testSession.setEndTime(LocalDateTime.now().plusHours(8));
    }
    
    @Test
    void getAllUsers_ShouldReturnAllUsersAndCacheThem() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(users);
        when(userCache.get(1L)).thenReturn(null);
        when(userCache.get(2L)).thenReturn(null);
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertEquals(2, result.size());
        verify(userCache, times(2)).put(anyLong(), any(User.class));
    }
    
    @Test
    void getAllUsers_ShouldUseCachedUsersWhenAvailable() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(users);
        when(userCache.get(1L)).thenReturn(testUser);
        when(userCache.get(2L)).thenReturn(null);
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertEquals(2, result.size());
        verify(userCache, times(1)).put(2L, testUser2); // Only cache the uncached user
    }
    
    @Test
    void getUserById_ShouldReturnUserFromCache() {
        // Arrange
        when(userCache.get(1L)).thenReturn(testUser);
        
        // Act
        User result = userService.getUserById(1L);
        
        // Assert
        assertEquals(testUser, result);
        verify(userRepository, never()).findById(anyLong());
    }
    
    @Test
    void getUserById_ShouldFetchFromRepositoryAndCacheWhenNotInCache() {
        // Arrange
        when(userCache.get(1L)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        User result = userService.getUserById(1L);
        
        // Assert
        assertEquals(testUser, result);
        verify(userCache).put(1L, testUser);
    }
    
    @Test
    void getUserById_ShouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        // Arrange
        when(userCache.get(1L)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }
    
    @Test
    void createUser_ShouldSaveAndCacheUser() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Act
        User result = userService.createUser(testUser);
        
        // Assert
        assertEquals(testUser, result);
        verify(userCache).put(1L, testUser);
    }
    
    @Test
    void createUser_ShouldThrowDuplicateEmailExceptionWhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(testUser));
    }
    
    @Test
    void deleteUser_ShouldRemoveFromCacheAndRepository() {
        // Arrange
        doNothing().when(userCache).remove(1L);
        doNothing().when(userRepository).deleteById(1L);
        
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userCache).remove(1L);
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void updateUser_ShouldUpdateUsername() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("newUsername");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, updatedUser);
        
        // Assert
        assertEquals("newUsername", result.getUsername());
        verify(userCache).put(1L, testUser);
    }
    
    @Test
    void updateUser_ShouldUpdateEmailWhenNotDuplicate() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, updatedUser);
        
        // Assert
        assertEquals("new@example.com", result.getEmail());
    }
    
    @Test
    void updateUser_ShouldThrowDuplicateEmailExceptionWhenEmailExists() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setEmail("existing@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.updateUser(1L, updatedUser));
    }
    
    @Test
    void updateUser_ShouldUpdateSleepAdvices() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setSleepAdvices(new HashSet<>(Collections.singleton(testAdvice)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(adviceRepository.findById(1L)).thenReturn(Optional.of(testAdvice));
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, updatedUser);
        
        // Assert
        assertEquals(1, result.getSleepAdvices().size());
        assertTrue(result.getSleepAdvices().contains(testAdvice));
    }
    
    @Test
    void updateUser_ShouldThrowResourceNotFoundExceptionWhenAdviceNotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setSleepAdvices(new HashSet<>(Collections.singleton(testAdvice)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(adviceRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updatedUser));
    }
    
    @Test
    void updateUser_ShouldUpdateSleepSessions() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setSleepSessions(new HashSet<>(Collections.singleton(testSession)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, updatedUser);
        
        // Assert
        assertEquals(1, result.getSleepSessions().size());
        Session updatedSession = result.getSleepSessions().iterator().next();
        assertNotNull(updatedSession.getStartTime());
        assertNotNull(updatedSession.getEndTime());
        assertTrue(updatedSession.getEndTime().isAfter(updatedSession.getStartTime()));
    }
    
    @Test
    void updateUser_ShouldThrowResourceNotFoundExceptionWhenSessionNotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setSleepSessions(new HashSet<>(Collections.singleton(testSession)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updatedUser));
    }
    
    @Test
    void updateUser_ShouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, new User()));
    }
    
    @Test
    void getUsersWithoutSessions_ShouldReturnUsersAndCacheThem() {
        // Arrange
        when(userRepository.findUsersWithoutSessions()).thenReturn(Collections.singletonList(testUser));
        when(userCache.get(1L)).thenReturn(null);
        
        // Act
        List<User> result = userService.getUsersWithoutSessions();
        
        // Assert
        assertEquals(1, result.size());
        verify(userCache).put(1L, testUser);
    }
    
    @Test
    void getUsersWithoutSessions_ShouldUseCachedUsersWhenAvailable() {
        // Arrange
        when(userRepository.findUsersWithoutSessions()).thenReturn(Collections.singletonList(testUser));
        when(userCache.get(1L)).thenReturn(testUser);
        
        // Act
        List<User> result = userService.getUsersWithoutSessions();
        
        // Assert
        assertEquals(1, result.size());
        verify(userCache, never()).put(1L, testUser);
    }
    
    @Test
    void getUsersWithoutAdvices_ShouldReturnUsersAndCacheThem() {
        // Arrange
        when(userRepository.findUsersWithoutAdvices()).thenReturn(Collections.singletonList(testUser));
        when(userCache.get(1L)).thenReturn(null);
        
        // Act
        List<User> result = userService.getUsersWithoutAdvices();
        
        // Assert
        assertEquals(1, result.size());
        verify(userCache).put(1L, testUser);
    }
    
    @Test
    void getUsersWithoutAdvices_ShouldUseCachedUsersWhenAvailable() {
        // Arrange
        when(userRepository.findUsersWithoutAdvices()).thenReturn(Collections.singletonList(testUser));
        when(userCache.get(1L)).thenReturn(testUser);
        
        // Act
        List<User> result = userService.getUsersWithoutAdvices();
        
        // Assert
        assertEquals(1, result.size());
        verify(userCache, never()).put(1L, testUser);
    }
    
    @Test
    void updateUsername_ShouldNotUpdateWhenUsernameIsNull() {
        // Arrange
        User newUser = new User();
        newUser.setUsername(null);
        
        // Act
        userService.updateUsername(testUser, newUser);
        
        // Assert
        assertEquals("testUser", testUser.getUsername());
    }
    
    @Test
    void updateUsername_ShouldNotUpdateWhenUsernameIsBlank() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("  ");
        
        // Act
        userService.updateUsername(testUser, newUser);
        
        // Assert
        assertEquals("testUser", testUser.getUsername());
    }
    
    @Test
    void updateEmail_ShouldNotUpdateWhenEmailIsNull() {
        // Arrange
        User newUser = new User();
        newUser.setEmail(null);
        
        // Act
        userService.updateEmail(testUser, newUser);
        
        // Assert
        assertEquals("test@example.com", testUser.getEmail());
    }
    
    @Test
    void updateEmail_ShouldNotUpdateWhenEmailIsBlank() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("  ");
        
        // Act
        userService.updateEmail(testUser, newUser);
        
        // Assert
        assertEquals("test@example.com", testUser.getEmail());
    }
    
    @Test
    void updateSleepAdvices_ShouldNotUpdateWhenAdvicesIsNull() {
        // Arrange
        User newUser = new User();
        newUser.setSleepAdvices(null);
        testUser.setSleepAdvices(new HashSet<>(Collections.singleton(testAdvice)));
        
        // Act
        userService.updateSleepAdvices(testUser, newUser);
        
        // Assert
        assertEquals(1, testUser.getSleepAdvices().size());
    }
    
    @Test
    void updateSleepAdvices_ShouldNotUpdateWhenAdvicesIsEmpty() {
        // Arrange
        User newUser = new User();
        newUser.setSleepAdvices(new HashSet<>(Collections.emptyList()));
        testUser.setSleepAdvices(new HashSet<>(Collections.singleton(testAdvice)));
        
        // Act
        userService.updateSleepAdvices(testUser, newUser);
        
        // Assert
        assertEquals(1, testUser.getSleepAdvices().size());
    }
    
    @Test
    void updateSleepSessions_ShouldNotUpdateWhenSessionsIsNull() {
        // Arrange
        User newUser = new User();
        newUser.setSleepSessions(null);
        testUser.setSleepSessions(new HashSet<>(Collections.singleton(testSession)));
        
        // Act
        userService.updateSleepSessions(testUser, newUser);
        
        // Assert
        assertEquals(1, testUser.getSleepSessions().size());
    }
    
    @Test
    void updateSleepSessions_ShouldNotUpdateWhenSessionsIsEmpty() {
        // Arrange
        User newUser = new User();
        newUser.setSleepSessions(new HashSet<>(Collections.emptyList()));
        testUser.setSleepSessions(new HashSet<>(Collections.singleton(testSession)));
        
        // Act
        userService.updateSleepSessions(testUser, newUser);
        
        // Assert
        assertEquals(1, testUser.getSleepSessions().size());
    }
}