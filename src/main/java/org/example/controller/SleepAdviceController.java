package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.model.SleepAdvice;
import org.example.service.SleepAdviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Советы", description = "Операции с советами для сна")
@RestController
@RequestMapping("/api/advices")
@RequiredArgsConstructor
public class SleepAdviceController {
    
    private final SleepAdviceService sleepAdviceService;
    
    @Operation(summary = "Получить все советы")
    @GetMapping
    public ResponseEntity<List<SleepAdvice>> getAllAdvices() {
        return ResponseEntity.ok(sleepAdviceService.getAllAdvices());
    }
    
    @Operation(summary = "Получить совет по {id}")
    @GetMapping("/{id}")
    public ResponseEntity<SleepAdvice> getAdviceById(@PathVariable Long id) {
        return ResponseEntity.ok(sleepAdviceService.getAdviceById(id));
    }
    
    @Operation(summary = "Создать новый совет")
    @PostMapping
    public ResponseEntity<SleepAdvice> createAdvice(@RequestBody SleepAdvice advice) {
        return ResponseEntity.ok(sleepAdviceService.createAdvice(advice));
    }
    
    @Operation(summary = "Обновить существующий совет")
    @PutMapping("/{id}")
    public ResponseEntity<SleepAdvice> updateAdvice(@PathVariable Long id,
                                                    @RequestBody SleepAdvice advice) {
        return ResponseEntity.ok(sleepAdviceService.updateAdvice(id, advice));
    }
    
    @Operation(summary = "Удалить совет")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvice(@PathVariable Long id) {
        sleepAdviceService.deleteAdvice(id);
        return ResponseEntity.noContent().build();
    }
}