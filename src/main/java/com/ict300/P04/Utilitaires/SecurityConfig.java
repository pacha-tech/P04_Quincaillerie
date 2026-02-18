package com.ict300.P04.Utilitaires;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import com.ict300.P04.Utilitaires.FirebaseFilter; // Vérifie que le package est exact
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF et CORS (CORS doit être géré par un Bean séparé ou @CrossOrigin)
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Autorisations publiques
                        .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()
                        .requestMatchers("/quincaillerie/auth/**").permitAll()
                        .requestMatchers("/quincaillerie/products/**").permitAll()
                        .requestMatchers("/quincaillerie/favorite/**").permitAll()
                        .requestMatchers("/quincaillerie/quincaillerie/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Autoriser les requêtes OPTIONS (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Tout le reste demande une authentification
                        .anyRequest().authenticated()
                )
                // 2. TRÈS IMPORTANT : Désactiver les mécanismes d'authentification par défaut
                .addFilterBefore(new FirebaseFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationToken.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable) // REMPLACÉ ICI

                // 3. Gestion de session : STATELESS (pas de cookies/sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

}
