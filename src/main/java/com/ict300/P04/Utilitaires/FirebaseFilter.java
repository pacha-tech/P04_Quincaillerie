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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

                String uid = decodedToken.getUid();
                String role = (String) decodedToken.getClaims().getOrDefault("role", "CLIENT");

                List authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(uid, null, authorities);

                // On injecte tous les claims (dont quincaillerieId) pour les récupérer dans le Controller
                auth.setDetails(decodedToken.getClaims());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            System.err.println("❌ Firebase Auth Error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}