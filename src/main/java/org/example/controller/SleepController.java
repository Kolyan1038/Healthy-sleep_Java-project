package org.example.controller;

import java.util.List;
import org.example.model.SleepAdvice;
import org.example.service.SleepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для предоставления советов по здоровому сну.
 */
@RestController
@RequestMapping("/api/sleep")
public class SleepController {
  
  private final SleepService sleepService;
  
  /**
   * Конструктор контроллера.
   *
   * @param sleepService сервис для обработки данных о сне
   */
  public SleepController(SleepService sleepService) {
    this.sleepService = sleepService;
  }
  
  /**
   * Получить все советы по здоровому сну.
   *
   * @return список всех советов
   */
  @GetMapping("/all")
  public List<SleepAdvice> getAllAdvices() {
    return sleepService.getAllAdvices();
  }
  
  /**
   * Получить совет по ID.
   *
   * @param id идентификатор совета
   * @return найденный совет
   */
  @GetMapping("/{id}")
  public SleepAdvice getAdviceById(@PathVariable int id) {
    return sleepService.getAdviceById(id);
  }
  
  /**
   * Получить советы по заданным параметрам сна.
   *
   * @param minHours минимальное количество часов сна (опционально)
   * @param maxHours максимальное количество часов сна (опционально)
   * @return список советов, соответствующих критериям
   */
  @GetMapping("/filter")
  public List<SleepAdvice> getFilteredAdvices(
      @RequestParam(required = false) Integer minHours,
      @RequestParam(required = false) Integer maxHours) {
    
    return sleepService.getFilteredAdvices(minHours, maxHours);
  }
  
}
