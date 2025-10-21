package com.investment.users.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Users microservice.
 *
 * <p>Configures HTTP security for the application: disables CSRF for stateless
 * APIs, uses stateless session management, exposes public endpoints for docs and
 * health checks, restricts admin endpoints to users with the ADMIN role, and
 * secures user endpoints (GET) and all other routes requiring authentication.
 * The configuration also enables JWT-based OAuth2 resource server support.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configure the Spring Security filter chain.
     *
     * <p>This method configures:
     * <ul>
     *   <li>CSRF disabled (suitable for stateless REST APIs)</li>
     *   <li>Session management set to STATELESS</li>
     *   <li>Publicly accessible paths for OpenAPI/Swagger and health checks</li>
     *   <li>Admin endpoints require ROLE_ADMIN</li>
     *   <li>GET requests to user endpoints require authentication</li>
     *   <li>All other requests require authentication</li>
     *   <li>OAuth2 resource server JWT support for bearer tokens</li>
     * </ul></p>
     *
     * @param http the {@link HttpSecurity} instance to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring security
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // aquí ya no da warning
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/actuator/health/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
