package org.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс для запуска Spring Boot приложения "Healthy Sleep".
 */
@OpenAPIDefinition(
        info = @Info(title = "Sleep Advice API", version = "1.0",
                description = "Документация API для рекомендаций по сну")
)
@SpringBootApplication
public class HealthySleepApplication {
    
    /**
     * Точка входа в Spring Boot приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(HealthySleepApplication.class, args);
    }
}