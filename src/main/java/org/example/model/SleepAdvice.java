package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, представляющий совет по здоровому сну.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SleepAdvice {
    
    /**
     * Текст совета по сну.
     */
    private String advice;
    
    /**
     * Рекомендуемое количество часов сна.
     */
    private int recommendedHours;
}