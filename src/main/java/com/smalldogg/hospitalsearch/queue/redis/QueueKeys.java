package com.smalldogg.hospitalsearch.queue.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueueKeys {

    public static String queueKey(String encId) {
        return "queue:" + encId;
    }

    public static String member(UUID ticketId) {
        return "ticket:" + ticketId;
    }

    public static String userKey(String encId, String sessionKey) {
        return "queue:user:" + encId + ":" + sessionKey;
    }
}
