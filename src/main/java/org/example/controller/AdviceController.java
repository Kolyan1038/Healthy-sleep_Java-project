package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.model.Advice;
import org.example.service.AdviceService;
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
@RequiredArgsConstructor
public class AdviceController {
    
    private final AdviceService adviceService;
    
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
    
    @Operation(summary = "Создать новый совет")
    @PostMapping
    public ResponseEntity<Advice> createAdvice(@RequestBody Advice advice) {
        return ResponseEntity.ok(adviceService.createAdvice(advice));
    }
    
    @Operation(summary = "Обновить существующий совет")
    @PutMapping("/{id}")
    public ResponseEntity<Advice> updateAdvice(@PathVariable Long id,
                                                    @RequestBody Advice advice) {
        return ResponseEntity.ok(adviceService.updateAdvice(id, advice));
    }
    
    @Operation(summary = "Удалить совет")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvice(@PathVariable Long id) {
        adviceService.deleteAdvice(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить все советы с подгрузкой связанных пользователей")
    @GetMapping("/with-users")
    public ResponseEntity<List<Advice>> getAllAdviceWithUsers() {
        return ResponseEntity.ok(adviceService.getAllAdviceWithUsers());
    }
    
    @Operation(summary = "Получить все советы, которые рекомендуют больше или меньше"
            + "указанного количества часов сна")
    @GetMapping("/by-hours-range")
    public ResponseEntity<List<Advice>> getAdvicesByRecommendedHoursRange(
            @RequestParam(required = false) Integer minHours,
            @RequestParam(required = false) Integer maxHours) {
        return ResponseEntity.ok(adviceService
                .getAdvicesByRecommendedHoursRange(minHours, maxHours));
    }
    
    @Operation(summary = "Получить все советы, которые рекомендуют"
            + "больше указанногоколичества часов сна")
    @GetMapping("/greater-than-hours")
    public ResponseEntity<List<Advice>> getAdvicesByRecommendedHoursGreaterThan(
            @RequestParam(required = false) Integer hours) {
        return ResponseEntity.ok(adviceService
                .getAdvicesByRecommendedHoursGreaterThan(hours));
    }
    
    @Operation(summary = "Получить все советы, которые рекомендуют"
            + "меньше указанного количества часов сна")
    @GetMapping("/less-than-hours")
    public ResponseEntity<List<Advice>> getAdvicesByRecommendedHoursLessThan(
            @RequestParam(required = false) Integer hours) {
        return ResponseEntity.ok(adviceService
                .getAdvicesByRecommendedHoursLessThan(hours));
    }
    
    @Operation(summary = "Получить советы, связанные с конкретным пользователем по {userId}")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Advice>> getAdvicesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adviceService.getAdvicesByUserId(userId));
    }
    
    @Operation(summary = "Получить советы, не связанные с пользователями")
    @GetMapping("/unassigned")
    public ResponseEntity<List<Advice>> getUnassignedAdvices() {
        return ResponseEntity.ok(adviceService.getUnassignedAdvices());
    }
    
    @Operation(summary = "Получить отсортированные советы"
            + "по количеству пользователей (от большего к меньшему)")
    @GetMapping("/ordered-by-user-count")
    public ResponseEntity<List<Advice>> getAllAdvicesOrderedByUserCountDesc() {
        return ResponseEntity.ok(adviceService.getAllAdvicesOrderedByUserCountDesc());
    }
}