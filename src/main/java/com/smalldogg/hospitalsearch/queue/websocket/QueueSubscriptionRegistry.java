package com.smalldogg.hospitalsearch.queue.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QueueSubscriptionRegistry {

    private final Map<String, Set<UUID>> byEncId = new HashMap<>();

    /**
     * byEncId에 encId key가 존재할 경우 기존 value return
     * 없을 경우, 정의된 람다식에 따라 ConcurrentHasMap에 newKeySet 정의
     * 후, ticketId 추가
     */
    public void add(String encId, UUID ticketId) {
        byEncId.computeIfAbsent(encId, k -> ConcurrentHashMap.newKeySet()).add(ticketId);
    }

    public void remove(String encId, UUID ticketId) {
        Set<UUID> set = byEncId.get(encId);
        if (set == null) return;
        set.remove(ticketId);
        if (set.isEmpty()) byEncId.remove(encId);
    }

    public Map<String, Set<UUID>> snapshot() {
        Map<String, Set<UUID>> copy = new HashMap<>();

        byEncId.forEach((encId, set) -> copy.put(encId, new HashSet<>(set)));
        return copy;
    }
}
