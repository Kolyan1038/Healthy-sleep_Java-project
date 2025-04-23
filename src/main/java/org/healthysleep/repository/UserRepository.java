package org.healthysleep.repository;

import java.util.List;
import org.healthysleep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN u.sleepSessions s WHERE s IS NULL")
    List<User> findUsersWithoutSessions();

    @Query("SELECT u FROM User u LEFT JOIN u.sleepAdvices s WHERE s IS NULL")
    List<User> findUsersWithoutAdvices();
}
