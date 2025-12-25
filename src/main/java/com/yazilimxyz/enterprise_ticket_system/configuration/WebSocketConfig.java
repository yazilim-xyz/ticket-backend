package com.yazilimxyz.enterprise_ticket_system.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.yazilimxyz.enterprise_ticket_system.entities.InternalChat;
import com.yazilimxyz.enterprise_ticket_system.security.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. SimpleBroker: Mesajları RAM'de tutan ve dağıtan mekanizma.
        // "/queue" ile başlayan mesajlar buraya gelir ve abonelere iletilir.
        // (Bizim kodumuzda "/queue" prefix'i, Spring tarafından rewrite edilmiş unique
        // adresler için kullanılır)
        registry.enableSimpleBroker("/queue");

        // 2. ApplicationDestinationPrefixes: Client'tan Server'a gelen mesajlar için
        // filtre.
        // "/app" ile başlayanlar @MessageMapping metodlarına yönlendirilir.
        registry.setApplicationDestinationPrefixes("/app");

        // 3. UserDestinationPrefix: İŞTE SİHİR BURADA!
        // Spring'e diyoruz ki: "/user" ile başlayan her şeyi sen özel olarak ele al.
        // Bu prefix, UserDestinationMessageHandler'ı devreye sokar.
        // Client "/user/queue/messages" dediğinde, Spring bunu
        // "/queue/messages-user<SessionID>"
        // şeklinde benzersiz bir adrese dönüştürür.
        registry.setUserDestinationPrefix("/user");
    }

    // GÜVENLİK VE KİMLİK EŞLEŞTİRME (Principal Binding)
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Eğer accessor null ise mesajı olduğu gibi geçir
                if (accessor == null) {
                    return message;
                }

                // Eğer komut CONNECT ise (Bağlanıyorsa)
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Header'dan "Authorization" değerini al
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7); // "Bearer " kısmını at
                        Long userId = jwtUtil.extractUserId(token);

                        if (userId != null) {
                            // TEKNİK DETAY: Principal Ataması
                            // Burada oluşturduğumuz Principal nesnesini (user), o anki WebSocket
                            // Session'ına yapıştırıyoruz.
                            // Spring'in "SimpUserRegistry"si bu bilgiyi kullanarak "ali ->
                            // SessionID:xyz-123" eşleşmesini yapar.
                            // convertAndSendToUser metodu çalışırken bu eşleşmeye bakar.
                            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                                    userId.toString(),
                                    null, List.of());
                            accessor.setUser(user);
                        }
                    } else {
                        System.out.println(">>> SOCKET BAĞLANTISI HATALI: Token yok veya geçersiz!");
                    }
                }
                return message;
            }
        });
    }
}
