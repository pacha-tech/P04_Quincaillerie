package com.ict300.P04.Utilitaires;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.FirebaseApp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class FirebaseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. On récupère le badge (Token) dans le header
        String header = request.getHeader("Authorization");

        // 2. Si pas de badge, on laisse passer (Spring Security bloquera plus tard si la route est privée)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            // 3. Sécurité : On vérifie que Firebase est bien initialisé avant d'appeler l'instance
            if (!FirebaseApp.getApps().isEmpty()) {

                // 4. On décode le token Google
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String uid = decodedToken.getUid();

                // 5. EXTRACTION DU RÔLE (Custom Claims)
                // On récupère le rôle "VENDEUR" ou "CLIENT" qu'on a mis lors de l'inscription
                String role = (String) decodedToken.getClaims().get("role");
                if (role == null) role = "CLIENT"; // Sécurité par défaut

                // 6. On crée les "Authorities" (Permissions) pour Spring
                // Le préfixe "ROLE_" est le standard de Spring Security
                List authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);

                // 7. On remplit le registre de sécurité (SecurityContext)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(uid, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // Si le token est expiré ou faux, on vide le contexte
            SecurityContextHolder.clearContext();
            System.err.println("❌ Erreur Firebase Token : " + e.getMessage());
        }

        // 8. On continue la chaîne vers le Controller
        filterChain.doFilter(request, response);
    }
}
