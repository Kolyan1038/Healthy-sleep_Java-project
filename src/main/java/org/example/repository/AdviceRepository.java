package org.example.repository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.example.model.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {
    List<Advice> findAllByRecommendedHours(
            @Min(
                    value = 1,
                    message = "Recommended hours must be at least 1")
            @Max(
                    value = 24,
                    message = "Recommended hours cannot exceed 24")
            int recommendedHours);
    
    List<Advice> findAllByRecommendedHoursGreaterThan(
            @Min(
                    value = 1,
                    message = "Recommended hours must be at least 1")
            @Max(
                    value = 24,
                    message = "Recommended hours cannot exceed 24")
            int recommendedHoursIsGreaterThan);
    
    List<Advice> findAllByRecommendedHoursLessThan(
            @Min(
                    value = 1,
                    message = "Recommended hours must be at least 1")
            @Max(
                    value = 24,
                    message = "Recommended hours cannot exceed 24")
            int recommendedHoursIsLessThan);
}