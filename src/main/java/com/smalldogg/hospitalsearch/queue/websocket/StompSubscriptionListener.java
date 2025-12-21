package com.smalldogg.hospitalsearch.queue.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.PathContainer;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StompSubscriptionListener {

    private final QueueSubscriptionRegistry registry;

    private final PathPattern pattern = new PathPatternParser().parse("/topic/queue/{encId}/{ticketId}");

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination == null) return;

        PathPattern.PathMatchInfo pathMatchInfo = pattern.matchAndExtract(PathContainer.parsePath(destination));
        if (pathMatchInfo == null) return;

        String encId = pathMatchInfo.getUriVariables().get("encId");
        String ticketIdStr = pathMatchInfo.getUriVariables().get("ticketId");

        try {
            UUID ticketId = UUID.fromString(ticketIdStr);
            registry.add(encId, ticketId);
        } catch (IllegalArgumentException e) {
        }
    }
}
