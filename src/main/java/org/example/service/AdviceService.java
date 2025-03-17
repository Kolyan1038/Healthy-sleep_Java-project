package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.cache.AdviceCache;
import org.example.exception.NotFoundException;
import org.example.model.Advice;
import org.example.repository.AdviceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdviceService {
    
    private final AdviceRepository adviceRepository;
    private final AdviceCache adviceCache;
    
    public List<Advice> getAllAdvices() {
        List<Advice> advices = adviceRepository.findAll();
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public Advice getAdviceById(Long id) {
        Advice cachedAdvice = adviceCache.get(id);
        if (cachedAdvice != null) {
            return cachedAdvice;
        }
        Advice advice = adviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Совет по сну не найден"));
        adviceCache.put(id, advice);
        return advice;
    }
    
    public Advice createAdvice(Advice advice) {
        Advice savedAdvice = adviceRepository.save(advice);
        adviceCache.put(savedAdvice.getId(), savedAdvice);
        return savedAdvice;
    }
    
    public Advice updateAdvice(Long id, Advice updatedAdvice) {
        Advice advice = getAdviceById(id);
        advice.setAdvice(updatedAdvice.getAdvice());
        advice.setRecommendedHours(updatedAdvice.getRecommendedHours());
        Advice savedAdvice = adviceRepository.save(advice);
        adviceCache.put(id, savedAdvice);
        return savedAdvice;
    }
    
    public List<Advice> getAdvicesByRecommendedHours(int recommendedHours) {
        List<Advice> advices = adviceRepository.findByRecommendedHours(recommendedHours);
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public void deleteAdvice(Long id) {
        adviceCache.remove(id);
        adviceRepository.deleteById(id);
    }
    
    public List<Advice> getAllAdviceWithUsers() {
        List<Advice> advices = adviceRepository.findAllWithUsers();
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getAdvicesByRecommendedHoursRange(int minHours, int maxHours) {
        List<Advice> advices =
                adviceRepository.findByRecommendedHoursRange(minHours, maxHours);
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getAdvicesByRecommendedHoursGreaterThan(int hours) {
        List<Advice> advices = adviceRepository.findByRecommendedHoursGreaterThan(hours);
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getAdvicesByRecommendedHoursLessThan(int hours) {
        List<Advice> advices = adviceRepository.findByRecommendedHoursLessThan(hours);
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getAdvicesByUserId(Long userId) {
        List<Advice> advices = adviceRepository.findByUserId(userId);
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getUnassignedAdvices() {
        List<Advice> advices = adviceRepository.findUnassignedAdvices();
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }
    
    public List<Advice> getAllAdvicesOrderedByUserCountDesc() {
        List<Advice> advices = adviceRepository.findAllOrderByUserCountDesc();
        for (Advice advice : advices) {
            if (adviceCache.get(advice.getId()) == null) {
                adviceCache.put(advice.getId(), advice);
            }
        }
        return advices;
    }

}