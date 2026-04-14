/*
package com.ict300.P04.Utilitaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Active la gestion des messages WebSocket via un "Broker"
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketTokenInterceptor webSocketTokenInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. Les destinations que le serveur "écoute" (Messages envoyés du Client vers le Serveur)
        // Si le client envoie à /app/chat, cela ira vers vos méthodes @MessageMapping
        config.setApplicationDestinationPrefixes("/envoyer");

        // 2. Les destinations auxquelles les clients s'abonnent (Messages envoyés du Serveur vers le Client)
        // /topic : pour les messages publics ou de groupe (ex: une conversation)
        // /queue : pour les messages privés (un utilisateur spécifique)
        config.enableSimpleBroker("/canal", "/prive");

        // 3. Préfixe pour les messages destinés à UN seul utilisateur spécifique
        // Spring l'utilise pour transformer /user/queue/messages en /queue/messages-user123 en interne
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // L'URL de connexion initiale (Handshake HTTP)
        // Le client (React/Angular) se connectera à : http://localhost:8080/ws-chat
        registry.addEndpoint("/connexion")
                .setAllowedOriginPatterns("*") // Autorise les connexions depuis n'importe quelle origine (à restreindre en production)
                .withSockJS(); // Active SockJS comme solution de repli si le navigateur ne supporte pas WebSocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenInterceptor);
    }
}
 */

package com.ict300.P04.Utilitaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy; // <-- NOUVEAU
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler; // <-- NOUVEAU
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler; // <-- NOUVEAU
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Active la gestion des messages WebSocket via un "Broker"
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketTokenInterceptor webSocketTokenInterceptor;

    // 🚀 --- DÉBUT DE L'AJOUT POUR LES HEARTBEATS --- 🚀
    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy ThreadPoolTaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }
    // 🚀 --- FIN DE L'AJOUT --- 🚀

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. Les destinations que le serveur "écoute"
        config.setApplicationDestinationPrefixes("/envoyer");

        // 2. Les destinations auxquelles les clients s'abonnent + 🚀 CONFIGURATION HEARTBEAT 🚀
        config.enableSimpleBroker("/canal", "/prive")
                .setTaskScheduler(this.messageBrokerTaskScheduler) // On attache le planificateur
                .setHeartbeatValue(new long[] {15000, 15000});     // On envoie un Ping/Pong toutes les 15 secondes

        // 3. Préfixe pour les messages destinés à UN seul utilisateur
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // L'URL de connexion initiale (Handshake HTTP)
        registry.addEndpoint("/connexion")
                .setAllowedOriginPatterns("*") // Autorise toutes les origines
                .withSockJS(); // Active SockJS
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenInterceptor);
    }
}

