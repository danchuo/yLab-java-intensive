package org.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The `WallerSpringApplication` class serves as the entry point for Spring Boot application.
 * It is annotated with `@SpringBootApplication`, which indicates that this class is a Spring Boot
 * application and that Spring Boot should perform component scanning to find and configure your
 * application's components.
 */
@SpringBootApplication
public class WallerSpringApplication {

  /**
   * The main method of the application, which is the starting point for Spring Boot
   * application.
   *
   * @param args The command-line arguments passed to the application.
   */
  public static void main(String... args) {
    SpringApplication.run(WallerSpringApplication.class, args);
  }
}
