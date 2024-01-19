package com.payten.restapi.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class POSChannelInterceptor implements ChannelInterceptor {
    @Autowired
    private JwtDecoder jwtDecoder;
    private static final Logger logger = LoggerFactory.getLogger(POSChannelInterceptor.class);



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                // Verify the token and set the authentication
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    System.out.println(jwt.getSubject());
                } catch (Exception e) {
                    logger.error("Failed to decode the access token from the STOMP headers. ", e);
                    throw e;
                }
            }
        }
        return message;
    }
}
