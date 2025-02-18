package org.example.service;

import java.util.List;
import org.example.exception.AdviceNotFoundException;
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
        List<SleepAdvice> advices = sleepRepository.getAllAdvices();
        if (id < 0 || id >= advices.size()) {
            throw new AdviceNotFoundException("Совет с ID " + id + " не найден.");
        }
        return advices.get(id);
    }
    
    /**
     * Получает советы, где количество рекомендуемых часов сна находится в заданном диапазоне.
     *
     * @param minHours минимальное количество часов сна
     * @param maxHours максимальное количество часов сна
     * @return список отфильтрованных советов
     */
    public List<SleepAdvice> getFilteredAdvices(Integer minHours, Integer maxHours) {
        if (minHours != null && (minHours < 1 || minHours > 24)
                || maxHours != null && (maxHours < 1 || maxHours > 24)) {
            throw new AdviceNotFoundException("Неверное кол-во сна.");
        }

        List<SleepAdvice> filteredAdvices = sleepRepository.getFilteredAdvices(minHours, maxHours);

        if (filteredAdvices.isEmpty()) {
            throw new AdviceNotFoundException("Советы по заданным параметрам не найдены.");
        }

        return filteredAdvices;
    }

}