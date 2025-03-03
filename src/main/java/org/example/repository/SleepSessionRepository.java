package org.example.repository;

import org.example.model.SleepSession;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сеансами сна.
 */
@Repository
public interface SleepSessionRepository extends JpaRepository<SleepSession, Long> {
    
    /**
     * Найти все сеансы сна пользователя.
     *
     * @param user объект пользователя
     * @return список сеансов сна
     */
    List<SleepSession> findByUser(User user);
}