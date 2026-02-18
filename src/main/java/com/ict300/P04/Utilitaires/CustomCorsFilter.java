package com.ict300.P04.Utilitaires;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;
import jakarta.servlet.*;


@Component
@Order(1)

public class CustomCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Autorise toutes les origines (indispensable pour le développement Flutter)
        response.setHeader("Access-Control-Allow-Origin", "*");
        // Autorise les méthodes HTTP classiques
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        // Autorise les headers dont Flutter a besoin (Content-Type, Authorization pour JWT)
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        // Durée de validité de cette configuration (en secondes)
        response.setHeader("Access-Control-Max-Age", "3600");

        // Gère la requête de "pré-vérification" (Preflight) envoyée par certains navigateurs/émulateurs
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Si tout est ok, on laisse passer la requête vers tes contrôleurs de quincaillerie
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}

