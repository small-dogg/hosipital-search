package com.smalldogg.hospitalsearch.queue.websocket;

import com.smalldogg.hospitalsearch.queue.HospitalReserveRepository;
import com.smalldogg.hospitalsearch.queue.entity.HospitalReserve;
import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import com.smalldogg.hospitalsearch.queue.out.QueueStatusMessage;
import com.smalldogg.hospitalsearch.queue.redis.RedisQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QueuePositionScheduler {

    private final QueueSubscriptionRegistry registry;
    private final HospitalReserveRepository reserveRepository;
    private final RedisQueueRepository redisQueueRepository;
    private final QueuePushSender pushSender;

    @Scheduled(fixedDelay = 3000)
    public void pushPositions() {
        Map<String, Set<UUID>> snap = registry.snapshot();

        for (Map.Entry<String, Set<UUID>> entry : snap.entrySet()) {
            String encId = entry.getKey();

            for (UUID ticketId : entry.getValue()) {
                reserveRepository.findByEncIdAndTicketId(encId, ticketId).ifPresent(reserve -> {
                    QueueStatusMessage msg = buildMessage(encId, reserve);
                    pushSender.sendToTicket(encId, ticketId, msg);
                });
            }
        }
    }

    private QueueStatusMessage buildMessage(String encId, HospitalReserve reserve) {
        HospitalReserveStatus status = reserve.getStatus();

        Integer position = null;
        String enterUrl = null;

        if (status == HospitalReserveStatus.WAITING) {
            position = redisQueueRepository.position(encId, reserve.getTicketId());
        }

        if (status == HospitalReserveStatus.READY) {
            enterUrl = "/hospitals/" + encId + "/reserve?ticketId=" + reserve.getTicketId();
        }

        return QueueStatusMessage.builder()
                .encId(encId)
                .ticketId(reserve.getTicketId())
                .status(status)
                .position(position)
                .readyDeadlineAt(reserve.getReadyDeadlineAt())
                .enterUrl(enterUrl)
                .build();
    }
}