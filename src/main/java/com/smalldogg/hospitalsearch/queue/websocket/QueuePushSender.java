package com.smalldogg.hospitalsearch.queue.websocket;

import com.smalldogg.hospitalsearch.queue.out.QueueStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QueuePushSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToTicket(String encId, UUID ticketId, QueueStatusMessage msg) {
        String dest = "/topic/queue/" + encId + "/" + ticketId;
        messagingTemplate.convertAndSend(dest, msg);
    }
}

