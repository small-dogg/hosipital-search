package com.smalldogg.hospitalsearch.queue.websocket;

import com.smalldogg.hospitalsearch.queue.HospitalReserveRepository;
import com.smalldogg.hospitalsearch.queue.entity.HospitalReserve;
import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import com.smalldogg.hospitalsearch.queue.out.QueueStatusMessage;
import com.smalldogg.hospitalsearch.queue.redis.RedisQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void expireReadyAndReleaseSlots() {
        LocalDateTime now = LocalDateTime.now();

        // 1) 만료 대상 조회 (batch)
        List<HospitalReserve> expired = reserveRepository.findReadyExpiredBatch(now, PageRequest.of(0, 200));
        if (expired.isEmpty()) return;

        // 2) 병원별로 그룹핑
        Map<String, List<HospitalReserve>> byEncId = expired.stream()
                .collect(Collectors.groupingBy(HospitalReserve::getEncId));

        // 3) 만료 처리 + 슬롯 반환 + 다음 승격
        for (Map.Entry<String, List<HospitalReserve>> e : byEncId.entrySet()) {
            String encId = e.getKey();
            List<HospitalReserve> list = e.getValue();

            for (HospitalReserve r : list) {
                r.markExpired(now);
            }

            // 만료된 수만큼 슬롯 반환 이벤트 기록 + 다음 WAITING READY 승격
            redisQueueRepository.releaseSlotsAndPromote(encId, list.size());

            for (HospitalReserve r : list) {
                pushSender.sendToTicket(encId, r.getTicketId(),
                        QueueStatusMessage.builder()
                                .encId(encId)
                                .ticketId(r.getTicketId())
                                .status(HospitalReserveStatus.EXPIRED)
                                .position(null)
                                .readyDeadlineAt(r.getReadyDeadlineAt())
                                .enterUrl(null)
                                .build());
            }
        }
    }
}