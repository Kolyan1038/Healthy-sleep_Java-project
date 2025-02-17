package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.example.model.SleepAdvice;
import org.example.service.SleepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sleep")
public class SleepController {
    
    private final SleepService sleepService;
    
    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }
    
    @GetMapping("/all")
    @Operation(summary = "Получить все советы по здоровому сну",
            description = "Возвращает список всех доступных советов.")
    public List<SleepAdvice> getAllAdvices() {
        return sleepService.getAllAdvices();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить совет по ID",
            description = "Возвращает конкретный совет по заданному идентификатору.")
    public SleepAdvice getAdviceById(
            @PathVariable @Parameter(description = "ID совета по сну") int id) {
        return sleepService.getAdviceById(id);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Получить советы по параметрам",
            description = "Фильтрует советы по минимальному и максимальному количеству часов сна.")
    public List<SleepAdvice> getFilteredAdvices(
            @RequestParam(required = false)
                @Parameter(description = "Минимальное количество часов сна") Integer minHours,
            @RequestParam(required = false)
                @Parameter(description = "Максимальное количество часов сна") Integer maxHours) {
        return sleepService.getFilteredAdvices(minHours, maxHours);
    }
}
