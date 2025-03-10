package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.exception.NotFoundException;
import org.example.model.SleepAdvice;
import org.example.repository.SleepAdviceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SleepAdviceService {
    
    private final SleepAdviceRepository sleepAdviceRepository;
    
    public List<SleepAdvice> getAllAdvices() {
        return sleepAdviceRepository.findAll();
    }
    
    public SleepAdvice getAdviceById(Long id) {
        return sleepAdviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Совет по сну не найден"));
    }
    
    public SleepAdvice createAdvice(SleepAdvice advice) {
        return sleepAdviceRepository.save(advice);
    }
    
    public SleepAdvice updateAdvice(Long id, SleepAdvice updatedAdvice) {
        SleepAdvice advice = getAdviceById(id);
        advice.setAdvice(updatedAdvice.getAdvice());
        advice.setRecommendedHours(updatedAdvice.getRecommendedHours());
        return sleepAdviceRepository.save(advice);
    }
    
    public void deleteAdvice(Long id) {
        sleepAdviceRepository.deleteById(id);
    }
}