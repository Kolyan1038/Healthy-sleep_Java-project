package org.example.service;

import java.util.List;
import org.example.model.SleepAdvice;
import org.example.repository.SleepRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с советами по здоровому сну.
 */
@Service
public class SleepService {
    private final SleepRepository sleepRepository;
    
    /**
     * Конструктор для инициализации репозитория.
     *
     * @param sleepRepository Репозиторий советов по сну
     */
    public SleepService(SleepRepository sleepRepository) {
        this.sleepRepository = sleepRepository;
    }
    
    /**
     * Получает список всех доступных советов по сну.
     *
     * @return список объектов SleepAdvice
     */
    public List<SleepAdvice> getAllAdvices() {
        return sleepRepository.getAllAdvices();
    }
    
    /**
     * Получает конкретный совет по его идентификатору.
     *
     * @param id идентификатор совета
     * @return объект SleepAdvice
     */
    public SleepAdvice getAdviceById(int id) {
        return sleepRepository.getAdviceById(id);
    }
    
    /**
     * Получает советы, где количество рекомендуемых часов сна находится в заданном диапазоне.
     *
     * @param minHours минимальное количество часов сна
     * @param maxHours максимальное количество часов сна
     * @return список отфильтрованных советов
     */
    public List<SleepAdvice> getFilteredAdvices(Integer minHours, Integer maxHours) {
        return sleepRepository.getFilteredAdvices(minHours, maxHours);
    }
}