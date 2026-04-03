package com.ict300.P04.Utilitaires;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class WebSocketTokenInterceptor implements ChannelInterceptor {

    @Override
    public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand()))) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // Utilisation de la même logique que ton FirebaseFilter
                    if (!com.google.firebase.FirebaseApp.getApps().isEmpty()) {
                        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                        String uid = decodedToken.getUid();

                        // Extraction du rôle (comme dans ton filtre)
                        String role = (String) decodedToken.getClaims().getOrDefault("role", "CLIENT");
                        var authorities = org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_" + role);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(uid, null, authorities);

                        // Très important : injecter les claims pour récupérer quincaillerieId plus tard
                        auth.setDetails(decodedToken.getClaims());

                        // On attache l'utilisateur au message STOMP
                        accessor.setUser(auth);
                        log.info("✅ WebSocket Authentifié : UID={} avec Rôle={}", uid, role);
                    }
                } catch (Exception e) {
                    log.error("❌ Erreur validation Token WebSocket: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}