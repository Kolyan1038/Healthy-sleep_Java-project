package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.SleepAdvice;
import org.example.service.SleepAdviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advices")
@RequiredArgsConstructor
public class SleepAdviceController {
    
    private final SleepAdviceService sleepAdviceService;
    
    @GetMapping
    public ResponseEntity<List<SleepAdvice>> getAllAdvices() {
        return ResponseEntity.ok(sleepAdviceService.getAllAdvices());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SleepAdvice> getAdviceById(@PathVariable Long id) {
        return ResponseEntity.ok(sleepAdviceService.getAdviceById(id));
    }
    
    @PostMapping
    public ResponseEntity<SleepAdvice> createAdvice(@RequestBody SleepAdvice advice) {
        return ResponseEntity.ok(sleepAdviceService.createAdvice(advice));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SleepAdvice> updateAdvice(@PathVariable Long id, @RequestBody SleepAdvice advice) {
        return ResponseEntity.ok(sleepAdviceService.updateAdvice(id, advice));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvice(@PathVariable Long id) {
        sleepAdviceService.deleteAdvice(id);
        return ResponseEntity.noContent().build();
    }
}