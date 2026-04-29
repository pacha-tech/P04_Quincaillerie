package com.ict300.P04.Utilitaires;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    private final FirebaseFilter firebaseFilter;

    public SecurityConfig(FirebaseFilter firebaseFilter) {
        this.firebaseFilter = firebaseFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Autorisations publiques
                        .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()
                        .requestMatchers("/quincaillerie/auth/**").permitAll()
                        .requestMatchers("/quincaillerie/favorite/**").authenticated()
                        .requestMatchers("/quincaillerie/users/profile").authenticated()
                        .requestMatchers("/quincaillerie/products/suggestions").permitAll()
                        .requestMatchers("/quincaillerie/products/search").permitAll()
                        .requestMatchers("/quincaillerie/products/recommendations").permitAll()
                        .requestMatchers("/quincaillerie/products/getProduct").permitAll()
                        .requestMatchers("/quincaillerie/products/addProduct").hasRole("VENDEUR")
                        .requestMatchers("/quincaillerie/products/getStock").hasRole("VENDEUR")
                        .requestMatchers(HttpMethod.DELETE , "/quincaillerie/products/**").hasRole("VENDEUR")
                        .requestMatchers("/quincaillerie/quincaillerie/details").permitAll()
                        .requestMatchers("/quincaillerie/quincaillerie/registerQuincaillerie").authenticated()
                        .requestMatchers("/quincaillerie/category/addCategory").hasRole("VENDEUR")
                        .requestMatchers("/quincaillerie/category/allCategory").permitAll()
                        .requestMatchers("/quincaillerie/promotion/allProductInPromotion").permitAll()
                        .requestMatchers("/quincaillerie/promotion/**").hasRole("VENDEUR")
                        .requestMatchers("/quincaillerie/panier/**").permitAll()
                        .requestMatchers("/quincaillerie/ping").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/connexion/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Autoriser les requêtes OPTIONS (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Tout le reste demande une authentification
                        .anyRequest().authenticated()
                )

                // Utilisation de l'instance injectée au lieu de "new"
                .addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // L'astuce magique : On autorise N'IMPORTE QUEL port localhost pour Flutter !
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://brixel-web.onrender.com"
                ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // On autorise tous les headers
        configuration.setAllowCredentials(true); // Indispensable si tu utilises des tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}