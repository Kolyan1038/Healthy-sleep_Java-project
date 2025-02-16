package org.example.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.example.model.SleepAdvice;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для хранения и получения советов по здоровому сну.
 */
@Repository
public class SleepRepository {
  
  private final List<SleepAdvice> adviceList = List.of(
      new SleepAdvice("Ложитесь спать до 23:00", 8),
      new SleepAdvice("Не используйте телефон перед сном", 7),
      new SleepAdvice("Спите в прохладной комнате", 7)
  );
  
  /**
   * Получить список всех советов по здоровому сну.
   *
   * @return список всех доступных советов
   */
  public List<SleepAdvice> getAllAdvices() {
    return adviceList;
  }
  
  /**
   * Получить совет по ID.
   *
   * @param id идентификатор совета
   * @return совет, если найден, иначе заглушка "Совет не найден"
   */
  public SleepAdvice getAdviceById(int id) {
    if (id >= 0 && id < adviceList.size()) {
      return adviceList.get(id);
    }
    return new SleepAdvice("Совет не найден", 0);
  }
  
  /**
   * Получить советы по заданным параметрам сна.
   *
   * @param minHours минимальное количество часов сна (опционально)
   * @param maxHours максимальное количество часов сна (опционально)
   * @return список советов, соответствующих критериям
   */
  public List<SleepAdvice> getFilteredAdvices(Integer minHours, Integer maxHours) {
    return adviceList.stream()
        .filter(advice -> (minHours == null || advice.getRecommendedHours() >= minHours)
            && (maxHours == null || advice.getRecommendedHours() <= maxHours))
        .collect(Collectors.toList());
  }
  
}
