package org.example.repository;

import java.util.List;
import org.example.model.SleepAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepAdviceRepository extends JpaRepository<SleepAdvice, Long> {
    List<SleepAdvice> findByRecommendedHours(int recommendedHours);
}