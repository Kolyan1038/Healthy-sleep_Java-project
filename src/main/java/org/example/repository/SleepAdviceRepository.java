package org.example.repository;

import org.example.model.SleepAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepAdviceRepository extends JpaRepository<SleepAdvice, Long> {
}