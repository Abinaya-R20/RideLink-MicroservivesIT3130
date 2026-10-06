package lk.ridelink.ride.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    // CHANGED (this whole method): the 401/403 JSON bodies below used to be
    // built with "\\"" inside a Java string, e.g. "{\\"timestamp\\":\\"".
    // In Java, \\ is already a complete escape for one backslash character,
    // so the very next " closed the string early. Everything after that
    // (the word timestamp, more backslashes) then sat outside any string,
    // which is not valid Java -- the file could not compile at all.
    // Fixed by building the same JSON with String.format("...\"%s\"...", ...),
    // which uses a single \" (correct escape for a quote) and is easier to
    // read/maintain than manual string concatenation.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                res.setContentType("application/json");
                res.getWriter().write(String.format(
                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"UNAUTHORIZED\",\"message\":\"Valid Bearer token required\",\"path\":\"%s\"}",
                    java.time.Instant.now(), req.getRequestURI()));
            }).accessDeniedHandler((req, res, ex) -> {
                res.setStatus(HttpStatus.FORBIDDEN.value());
                res.setContentType("application/json");
                res.getWriter().write(String.format(
                    "{\"timestamp\":\"%s\",\"status\":403,\"error\":\"FORBIDDEN\",\"message\":\"Insufficient role\",\"path\":\"%s\"}",
                    java.time.Instant.now(), req.getRequestURI()));
            }))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
