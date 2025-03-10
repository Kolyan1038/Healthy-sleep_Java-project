package org.example.repository;

import java.util.List;
import org.example.model.SleepSession;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepSessionRepository extends JpaRepository<SleepSession, Long> {
    
    List<SleepSession> findByUser(User user);
}