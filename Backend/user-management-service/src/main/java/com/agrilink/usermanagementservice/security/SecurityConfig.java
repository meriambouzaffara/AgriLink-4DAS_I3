package com.agrilink.usermanagementservice.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SecurityConfig {

    // Active @PreAuthorize("hasRole('ADMIN')") seulement en mode JWT
    @Configuration
    @EnableMethodSecurity
    @ConditionalOnProperty(name = "app.security.dev-mode", havingValue = "false", matchIfMissing = true)
    static class MethodSecurityConfig {
    }

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    // ---------- MODE DEVELOPPEMENT (TEMPORAIRE) : tout est ouvert ----------
    @Bean
    @ConditionalOnProperty(name = "app.security.dev-mode", havingValue = "true")
    SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }

    // ---------- MODE NORMAL : JWT Keycloak ----------
    @Bean
    @ConditionalOnProperty(name = "app.security.dev-mode", havingValue = "false", matchIfMissing = true)
    SecurityFilterChain jwtFilterChain(HttpSecurity http, JwtAuthConverter converter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // API sans session ni cookie
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> {
                    if (h2ConsoleEnabled) {
                        a.requestMatchers("/h2-console/**").permitAll();   // DEV UNIQUEMENT
                    }
                    a.requestMatchers("/error").permitAll();
                    a.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter.build()))
                        .authenticationEntryPoint((request, response, ex) ->
                                writeError(response, 401, "Unauthorized",
                                        "Jeton absent, invalide ou expire", request.getRequestURI()))
                        .accessDeniedHandler((request, response, ex) ->
                                writeError(response, 403, "Forbidden",
                                        "Acces refuse : role insuffisant", request.getRequestURI())));

        if (h2ConsoleEnabled) {
            http.headers(h -> h.frameOptions(f -> f.sameOrigin()));   // la console H2 utilise des frames
        }
        return http.build();
    }

    // Autorise le frontend Angular (en developpement)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Reponse JSON coherente avec ErrorResponse pour les erreurs 401 et 403
    private void writeError(HttpServletResponse response, int status, String error,
                            String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String safePath = path.replace("\\", "").replace("\"", "");
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(), status, error, message, safePath));
    }
}