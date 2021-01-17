package com.kuke.parkingticket.handler;

import com.kuke.parkingticket.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("StompHandler.preSend");
        if(accessor.getCommand() == StompCommand.CONNECT) {
            if(!jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token")))
                throw new AccessDeniedException("");
        }
        return message;
    }
}
