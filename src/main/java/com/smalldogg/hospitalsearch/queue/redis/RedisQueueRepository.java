package com.smalldogg.hospitalsearch.queue.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisQueueRepository {
    private final RedissonClient redissonClient;

    public boolean tryRegisterActiveUser(String encId, String sessionKey, UUID ticketId, Duration ttl) {
        RBucket<String> bucket = redissonClient.getBucket(QueueKeys.userKey(encId, sessionKey));
        return bucket.setIfAbsent(QueueKeys.userKey(encId, sessionKey), ttl);
    }

    public void enqueue(String encId, UUID ticketId, double score) {
        RScoredSortedSet<Object> zset = redissonClient.getScoredSortedSet(QueueKeys.queueKey(encId));
        zset.add(score, QueueKeys.member(ticketId));
    }

    public void remove(String encId, UUID ticketId) {
        RScoredSortedSet<String> zset = redissonClient.getScoredSortedSet(QueueKeys.queueKey(encId));
        zset.remove(QueueKeys.member(ticketId));
    }

    public Integer position(String encId, UUID ticketId) {
        RScoredSortedSet<String> zset = redissonClient.getScoredSortedSet(QueueKeys.queueKey(encId));
        Integer rank = zset.rank(QueueKeys.member(ticketId)); // 0-based
        return rank == null ? null : rank + 1;
    }

    public List<UUID> popFront(String encId, int n) {
        RScoredSortedSet<String> zset = redissonClient.getScoredSortedSet(QueueKeys.queueKey(encId));
        Collection<String> members = zset.pollFirst(n); // 앞에서 n개 원자적으로 꺼냄
        if (members == null || members.isEmpty()) return List.of();
        return members.stream()
                .map(m -> UUID.fromString(m.replace("ticket:", "")))
                .collect(Collectors.toList());
    }

    public void releaseSlotsAndPromote(String encId, int size) {

    }
}
