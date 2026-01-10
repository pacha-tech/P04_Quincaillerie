package com.ict300.P04.Utilitaires;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactivé pour faciliter les tests API
                .authorizeHttpRequests(auth -> auth
                        // 1. Autoriser la racine (pour Render) et les ressources statiques
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index.html", "/static/**", "/favicon.ico").permitAll()

                        // 2. Tes routes spécifiques
                        .requestMatchers("/quincaillerie/auth/**").permitAll()
                        .requestMatchers("/quincaillerie/products/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        // 3. Swagger et Documentation
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()

                        // 4. Autoriser les requêtes de pré-vérification (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Tout le reste demande une authentification
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // Désactive le formulaire de login redirection
                .httpBasic(Customizer.withDefaults()); // Garde l'auth basique pour le reste

        return http.build();
    }
}
