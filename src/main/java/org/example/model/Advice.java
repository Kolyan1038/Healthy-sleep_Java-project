package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "advices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Advice {

    @PreRemove
    private void preRemove() {
        for (User user : users) {
            user.getSleepAdvices().remove(this);
        }
        users.clear();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Advice text is required")
    @Size(min = 10, max = 1000, message = "Advice must be between 10 and 1000 characters")
    @Column(nullable = false)
    private String advice;
    
    @Min(value = 1, message = "Recommended hours must be at least 1")
    @Max(value = 24, message = "Recommended hours cannot exceed 24")
    @Column(nullable = false)
    private int recommendedHours;
    
    @ManyToMany(mappedBy = "sleepAdvices", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}