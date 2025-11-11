package com.investment.portfolios.configuration;

import com.investment.portfolios.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Spring Security configuration for the Portfolios microservice.
 *
 * <p>This configuration exposes a {@link SecurityFilterChain} bean that:
 * - Disables CSRF protection because the API is stateless.
 * - Configures authorization rules to allow unauthenticated access to
 *   actuator health/info and OpenAPI/Swagger endpoints.
 * - Requires authentication for application APIs under "/api/**".
 * - Denies any other requests by default.
 * - Enables OAuth2 resource server support with JWT validation.
 *
 * <p>Placeholders for further customization:
 * - Add CORS configuration if the API will be called from browsers.
 * - Replace permitAll/denyAll rules with role-based checks if needed.
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates the primary {@link SecurityFilterChain} bean used by Spring Security.
     *
     * <p>The returned filter chain configures:
     * - CSRF disabled (suitable for stateless REST APIs).
     * - Publicly accessible endpoints: actuator health/info and OpenAPI/Swagger UI.
     * - Authentication required for endpoints matching {@code /api/**}.
     * - All other requests are denied.
     * - OAuth2 Resource Server JWT support with default JWT configuration.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return a built {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs while building the security filter chain
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                Constants.HEALTH_CHECK_PATH,
                                Constants.HEALTH_INFO,
                                Constants.API_DOCS_VERSION_THREE,
                                Constants.SWAGGER_TWO,
                                Constants.SWAGGER_ONE
                        ).permitAll()
                        .requestMatchers(Constants.API).authenticated()
                        .anyRequest().denyAll()
                )
                // Resource Server (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}