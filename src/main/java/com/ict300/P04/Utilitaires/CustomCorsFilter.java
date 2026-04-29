/*
package com.ict300.P04.Utilitaires;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)
public class CustomCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // CORRECTION ICI : On met uniquement l'URL exacte du navigateur
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5000");

        // Autorise les méthodes HTTP classiques
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");

        // Autorise les headers dont Flutter a besoin
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin");

        // Autorise l'envoi de cookies ou de tokens d'authentification
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Durée de validité de cette configuration (en secondes)
        response.setHeader("Access-Control-Max-Age", "3600");

        // Gère la requête de "pré-vérification" (Preflight) envoyée par le navigateur
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Si tout est ok, on laisse passer la requête vers tes contrôleurs
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
 */