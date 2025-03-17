package org.example.repository;

import java.util.List;
import org.example.model.Session;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.startTime >= CURRENT_DATE")
    List<Session> findUserSessionsFromToday(@Param("userId") Long userId);
    
    List<Session> findByUser(User user);
}

