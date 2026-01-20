package com.vishwa.companyms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ---------------- PUBLIC ENDPOINTS ----------------
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET, "/api/v1/companies"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/companies/legacy",
                                "/api/v1/companies/search",
                                "/api/v1/companies/top-rated",
                                "/api/v1/companies/featured",
                                "/api/v1/companies/{companyId}",
                                "/api/v1/companies/{companyId}/stats"
                        ).permitAll()

                        // ---------------- INTERNAL ----------------
                        .requestMatchers("/api/v1/companies/internal/**").permitAll()

                        // ---------------- ADMIN & RECRUITER ----------------
                        .requestMatchers("/api/v1/companies/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/companies/recruiter/**").hasRole("RECRUITER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/companies").hasRole("ADMIN")
                        .requestMatchers("/api/v1/companies/**").hasRole("ADMIN")

                        // ---------------- SWAGGER ----------------
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
