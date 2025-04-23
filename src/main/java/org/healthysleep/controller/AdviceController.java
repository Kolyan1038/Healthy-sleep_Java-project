package org.healthysleep.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.healthysleep.model.Advice;
import org.healthysleep.service.AdviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Советы", description = "Операции с советами для сна")
@RestController
@RequestMapping("/api/advices")
public class AdviceController {
    
    private final AdviceService adviceService;
    
    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }
    
    @Operation(summary = "Получить все советы")
    @GetMapping
    public ResponseEntity<List<Advice>> getAllAdvices(
            @RequestParam(required = false) Integer recommendedHours) {
        if (recommendedHours != null) {
            // Если параметр передан, возвращаем отфильтрованные советы
            return ResponseEntity.ok(adviceService
                    .getAdvicesByRecommendedHours(recommendedHours));
        } else {
            // Если параметр не передан, возвращаем все советы
            return ResponseEntity.ok(adviceService.getAllAdvices());
        }
    }
    
    @Operation(summary = "Получить совет по {id}")
    @GetMapping("/{id}")
    public ResponseEntity<Advice> getAdviceById(@PathVariable Long id) {
        return ResponseEntity.ok(adviceService.getAdviceById(id));
    }
    
    @Operation(summary = "Создать несколько советов")
    @PostMapping("/bulk")
    public ResponseEntity<List<Advice>> createAdvices(@RequestBody List<Advice> advices) {
        List<Advice> savedAdvices = advices.stream()
                .map(adviceService::createAdvice)
                .collect(Collectors.toList());
        return ResponseEntity.ok(savedAdvices);
    }
    
    @Operation(summary = "Создать новый совет")
    @PostMapping
    public ResponseEntity<Advice> createAdvice(@Valid @RequestBody Advice advice) {
        return ResponseEntity.ok(adviceService.createAdvice(advice));
    }
    
    @Operation(summary = "Обновить существующий совет")
    @PutMapping("/{id}")
    public ResponseEntity<Advice> updateAdvice(@PathVariable Long id,
                                                   @Valid @RequestBody Advice advice) {
        return ResponseEntity.ok(adviceService.updateAdvice(id, advice));
    }
    
    @Operation(summary = "Удалить совет")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvice(@PathVariable Long id) {
        adviceService.deleteAdvice(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить все советы, которые рекомендуют "
            + "больше указанногоколичества часов сна")
    @GetMapping("/greater-than-hours")
    public ResponseEntity<List<Advice>> getAdvicesByRecommendedHoursGreaterThan(
            @RequestParam(required = false) Integer hours) {
        return ResponseEntity.ok(adviceService
                .getAdvicesByRecommendedHoursGreaterThan(hours));
    }
    
    @Operation(summary = "Получить все советы, которые рекомендуют "
            + "меньше указанного количества часов сна")
    @GetMapping("/less-than-hours")
    public ResponseEntity<List<Advice>> getAdvicesByRecommendedHoursLessThan(
            @RequestParam(required = false) Integer hours) {
        return ResponseEntity.ok(adviceService
                .getAdvicesByRecommendedHoursLessThan(hours));
    }
}