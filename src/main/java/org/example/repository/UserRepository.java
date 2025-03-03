package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Найти пользователя по имени.
     *
     * @param username имя пользователя
     * @return объект User, если найден
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Проверяет, существует ли пользователь с указанной почтой.
     *
     * @param email email пользователя
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);
    
}
