package com.investment.portfolios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Portfolios microservice.
 *
 * <p>This class serves as the entry point for the Spring Boot runtime.
 * When executed, it boots the Spring application context and starts the
 * embedded web server (if configured). Component scanning and auto-configuration
 * are enabled via the {@code @SpringBootApplication} meta-annotation.
 * Typical usage:
 * <pre>
 *     java -jar portfolios-ms-0.0.1-SNAPSHOT.jar
 * </pre>
 *
 * Responsibility:
 * - Bootstraps the Spring context.
 * - Triggers auto-configuration and component scanning for the application package.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@SpringBootApplication
public class PortfoliosMsApplication {

    /**
     * Application entry point.
     *
     * <p>Delegates to {@link SpringApplication#run(Class, String[])}
     * to launch the Spring Boot application. Any command-line arguments passed to the JVM
     * are forwarded to the application and can be used for externalized configuration or
     * conditional startup behavior.
     *
     * @param args command-line arguments passed to the application (may be empty)
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static void main(String[] args) {
        SpringApplication.run(PortfoliosMsApplication.class, args);
    }
}
