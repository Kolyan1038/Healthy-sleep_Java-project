package org.example.repository;

import java.util.List;
import org.example.model.User;
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

    @Query(value = "SELECT u.* FROM users u "
            + "LEFT JOIN user_advice ua "
            + "ON u.id = ua.user_id "
            + "WHERE ua.advice_id IS NULL", nativeQuery = true)
    List<User> findWithoutAdvicesNative();
}
