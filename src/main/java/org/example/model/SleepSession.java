package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность сеанса сна пользователя.
 */
@Entity
@Table(name = "sleep_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SleepSession {
    
    /**
     * Уникальный идентификатор сеанса сна.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Время начала сна.
     */
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    /**
     * Время окончания сна.
     */
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    /**
     * Пользователь, к которому привязан сеанс сна.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}