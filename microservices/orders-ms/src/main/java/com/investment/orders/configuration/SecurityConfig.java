package com.investment.orders.configuration;

import com.investment.orders.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Spring Security configuration for the orders' microservice.
 *
 * <p>This configuration defines the security filter chain for HTTP requests. It:
 * - Disables CSRF protection (suitable for stateless APIs),
 * - Permits unauthenticated access to API documentation and actuator health/info endpoints,
 * - Requires JWT authentication for all other endpoints,
 * - Configures the application as an OAuth2 Resource Server using JWTs.</p>
 *
 * Note: The exact endpoints allowed anonymously are defined in {@code Constants} to keep
 * routing and security rules centralized and configurable.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since November 10, 2025
 */
@Configuration
public class SecurityConfig {

    /**
     * Builds and returns the {@link SecurityFilterChain} that applies the security rules.
     *
     * <p>The method configures the provided {@link HttpSecurity} instance to:
     * - disable CSRF (useful for APIs that are protected via tokens),
     * - permit all requests to Swagger/OpenAPI endpoints and health/info actuator paths,
     * - require authentication (JWT) for any other request,
     * - enable JWT-based OAuth2 resource server support.</p>
     *
     * @param http the {@link HttpSecurity} instance used to configure web security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while building the security filter chain
     *
     * @author Remus-Ciprian Cotunoaea
     * @since November 10, 2025
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Swagger / actuator open to unauthenticated access
                        .requestMatchers(
                                Constants.API_DOCS_VERSION_THREE, Constants.SWAGGER_TWO, Constants.SWAGGER_ONE,
                                Constants.HEALTH_CHECK_PATH, Constants.HEALTH_INFO).permitAll()
                        // rest requires JWT authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}