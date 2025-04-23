package org.healthysleep.service;

import org.healthysleep.cache.AdviceCache;
import org.healthysleep.exception.ResourceNotFoundException;
import org.healthysleep.model.Advice;
import org.healthysleep.repository.AdviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdviceServiceTest {
    
    @Mock
    private AdviceRepository adviceRepository;
    
    @Mock
    private AdviceCache adviceCache;
    
    @InjectMocks
    private AdviceService adviceService;
    
    private Advice advice1;
    private Advice advice2;
    
    @BeforeEach
    void setUp() {
        advice1 = new Advice(1L, "Sleep well", 8);
        advice2 = new Advice(2L, "Relax before bed", 7);
    }
    
    @Test
    void getAllAdvices_ShouldCacheMissedItems() {
        // Arrange
        List<Advice> advices = Arrays.asList(advice1, advice2);
        when(adviceRepository.findAll()).thenReturn(advices);
        when(adviceCache.get(1L)).thenReturn(null);
        when(adviceCache.get(2L)).thenReturn(advice2);
        
        // Act
        List<Advice> result = adviceService.getAllAdvices();
        
        // Assert
        assertEquals(2, result.size());
        verify(adviceCache).put(1L, advice1);
        verify(adviceCache, never()).put(2L, advice2);
    }
    
    @Test
    void getAdviceById_WhenCached_ReturnFromCache() {
        // Arrange
        when(adviceCache.get(1L)).thenReturn(advice1);
        
        // Act
        Advice result = adviceService.getAdviceById(1L);
        
        // Assert
        assertEquals(advice1, result);
        verify(adviceRepository, never()).findById(any());
    }
    
    @Test
    void getAdviceById_WhenNotCached_ReturnFromRepositoryAndCache() {
        // Arrange
        when(adviceCache.get(1L)).thenReturn(null);
        when(adviceRepository.findById(1L)).thenReturn(Optional.of(advice1));
        
        // Act
        Advice result = adviceService.getAdviceById(1L);
        
        // Assert
        assertEquals(advice1, result);
        verify(adviceCache).put(1L, advice1);
    }
    
    @Test
    void getAdviceById_WhenNotFound_ThrowException() {
        // Arrange
        when(adviceCache.get(999L)).thenReturn(null);
        when(adviceRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                adviceService.getAdviceById(999L));
    }
    
    @Test
    void createAdvice_ShouldSaveAndCache() {
        // Arrange
        when(adviceRepository.save(advice1)).thenReturn(advice1);
        
        // Act
        Advice result = adviceService.createAdvice(advice1);
        
        // Assert
        verify(adviceCache).put(1L, advice1);
        assertEquals(advice1, result);
    }
    
    @Test
    void updateAdvice_ShouldUpdateAndCache() {
        // Arrange
        Advice updated = new Advice(1L, "Updated", 9);
        when(adviceRepository.findById(1L)).thenReturn(Optional.of(advice1));
        when(adviceRepository.save(any())).thenReturn(updated);
        
        // Act
        Advice result = adviceService.updateAdvice(1L, updated);
        
        // Assert
        assertEquals("Updated", result.getAdvice());
        assertEquals(9, result.getRecommendedHours());
    }
    
    @Test
    void deleteAdvice_ShouldRemoveFromCacheAndRepository() {
        // Act
        adviceService.deleteAdvice(1L);
        
        // Assert
        verify(adviceCache).remove(1L);
        verify(adviceRepository).deleteById(1L);
    }
    
    @Test
    void getAdvicesByRecommendedHours_ShouldCacheMissedItems() {
        // Arrange
        int hours = 8;
        List<Advice> advices = List.of(advice1);
        when(adviceRepository.findAllByRecommendedHours(hours)).thenReturn(advices);
        when(adviceCache.get(1L)).thenReturn(null);
        
        // Act
        List<Advice> result = adviceService.getAdvicesByRecommendedHours(hours);
        
        // Assert
        assertEquals(1, result.size());
        verify(adviceCache).put(1L, advice1);
    }
    
    @Test
    void getAdvicesByRecommendedHoursGreaterThan_ShouldCacheNewItems() {
        // Arrange
        int hours = 7;
        List<Advice> advices = List.of(advice1, advice2);
        when(adviceRepository.findAllByRecommendedHoursGreaterThan(hours)).thenReturn(advices);
        when(adviceCache.get(1L)).thenReturn(null);
        when(adviceCache.get(2L)).thenReturn(advice2);
        
        // Act
        List<Advice> result = adviceService.getAdvicesByRecommendedHoursGreaterThan(hours);
        
        // Assert
        assertEquals(2, result.size());
        verify(adviceCache).put(1L, advice1);
        verify(adviceCache, never()).put(2L, advice2);
    }
    
    @Test
    void getAdvicesByRecommendedHoursLessThan_ShouldHandleEmptyCache() {
        // Arrange
        int hours = 9;
        List<Advice> advices = List.of(advice2);
        when(adviceRepository.findAllByRecommendedHoursLessThan(hours)).thenReturn(advices);
        when(adviceCache.get(2L)).thenReturn(null);
        
        // Act
        List<Advice> result = adviceService.getAdvicesByRecommendedHoursLessThan(hours);
        
        // Assert
        assertEquals(1, result.size());
        verify(adviceCache).put(2L, advice2);
    }
    
    @Test
    void updateAdvice_NonExistingId_ShouldThrowException() {
        // Arrange
        Long invalidId = 999L;
        when(adviceRepository.findById(invalidId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                adviceService.updateAdvice(invalidId, new Advice()));
    }
    
    @Test
    void getAllAdvices_WithEmptyRepository_ReturnEmptyList() {
        // Arrange
        when(adviceRepository.findAll()).thenReturn(List.of());
        
        // Act
        List<Advice> result = adviceService.getAllAdvices();
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void createAdvice_WithNullArgument_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                adviceService.createAdvice(null));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-5, 0, 24})
    void getAdvicesByRecommendedHours_BoundaryValues(int hours) {
        // Arrange
        when(adviceRepository.findAllByRecommendedHours(hours)).thenReturn(List.of());
        
        // Act
        List<Advice> result = adviceService.getAdvicesByRecommendedHours(hours);
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void deleteAdvice_VerifyCacheInvocationOrder() {
        // Arrange
        doNothing().when(adviceCache).remove(1L);
        doNothing().when(adviceRepository).deleteById(1L);
        
        // Act
        adviceService.deleteAdvice(1L);
        
        // Assert
        InOrder inOrder = inOrder(adviceCache, adviceRepository);
        inOrder.verify(adviceCache).remove(1L);
        inOrder.verify(adviceRepository).deleteById(1L);
    }
    
    // Тест для проверки кэширования при повторных вызовах
    @Test
    void getAdviceById_MultipleCalls_ShouldCacheProperly() {
        // Arrange
        when(adviceCache.get(1L))
                .thenReturn(null)  // Первый вызов - нет в кэше
                .thenReturn(advice1); // Второй вызов - есть в кэше
        when(adviceRepository.findById(1L)).thenReturn(Optional.of(advice1));
        
        // Первый вызов
        Advice firstResult = adviceService.getAdviceById(1L);
        // Второй вызов
        Advice secondResult = adviceService.getAdviceById(1L);
        
        // Assert
        verify(adviceRepository, times(1)).findById(1L); // Репозиторий вызывается только 1 раз
        assertEquals(advice1, firstResult);
        assertEquals(advice1, secondResult);
    }
}