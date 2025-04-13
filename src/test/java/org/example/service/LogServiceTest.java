package org.example.service;

import org.example.exception.InvalidInputException;
import org.example.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    
    @InjectMocks
    private LogService logService;
    
    @Test
    void getLogFileForDate_InvalidDate_ThrowException() {
        assertThrows(InvalidInputException.class, () ->
                logService.getLogFileForDate("invalid-date"));
    }
    
    @Test
    void getLogFileForDate_FileNotExists_ThrowException() {
        assertThrows(ResourceNotFoundException.class, () ->
                logService.getLogFileForDate("01.01.2023"));
    }
    
    @Test
    void getDownloadFileName_ValidDate_ReturnsCorrectFormat() {
        String result = logService.getDownloadFileName("01.01.2023");
        assertEquals("app-2023-01-01.log", result);
    }
}