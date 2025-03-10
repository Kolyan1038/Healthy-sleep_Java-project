package org.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(title = "Sleep Advice API", version = "1.0",
                description = "Документация API для рекомендаций по сну")
)
@SpringBootApplication
public class HealthySleepApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthySleepApplication.class, args);
    }
}