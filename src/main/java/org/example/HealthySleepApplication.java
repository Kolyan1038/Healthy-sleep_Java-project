package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс для запуска Spring Boot приложения "Healthy Sleep".
 */
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
