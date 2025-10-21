package com.investment.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Users microservice Spring Boot application.
 *
 * <p>This class boots the Spring context and starts the embedded web server.
 * Keep this class minimal — configuration and beans should be defined in
 * configuration classes or component-scanned beans.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@SpringBootApplication
public class UserMsApplication {

    /**
     * Main method used to run the Spring Boot application.
     *
     * @param args application arguments passed from the command line
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    public static void main(String[] args) {
        SpringApplication.run(UserMsApplication.class, args);
    }
}
