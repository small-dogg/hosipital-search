package com.smalldogg.hospitalsearch.queue;

import com.smalldogg.hospitalsearch.queue.entity.HospitalReserve;
import com.smalldogg.hospitalsearch.queue.entity.HospitalSlotEvent;
import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import com.smalldogg.hospitalsearch.queue.enums.HospitalSlotEventType;
import com.smalldogg.hospitalsearch.queue.in.ExpireReadyParam;
import com.smalldogg.hospitalsearch.queue.in.OpenSlotsParam;
import com.smalldogg.hospitalsearch.queue.in.ReserveHospitalParam;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class HospitalHospitalReserveServiceImpl implements HospitalReserveService {

    private static final int READY_EXPIRE_MINUTES = 2;

    private final HospitalReserveRepository hospitalReserveRepository;
    private final HospitalSlotEventRepository  hospitalSlotEventRepository;
    private final RedissonClient redissonClient;

    /**
     * 대기열에 등록하되, 동일한 인원에 여러번 대기열에 참여할 수 없도록 제한
     */
    @Transactional
    @Override
    public UUID join(ReserveHospitalParam param) {
        //중복 대기 방지

        boolean exists = hospitalReserveRepository.existsByEncIdAndSessionKeyAndStatusIn(
                param.getEncId(),
                param.getSessionKey(),
                List.of(HospitalReserveStatus.WAITING, HospitalReserveStatus.READY)
        );

        if (exists) {
            throw new IllegalStateException("이미 대기열에 존재하는 상태입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        UUID ticketId = UUID.randomUUID();

        HospitalReserve hospitalReserve = HospitalReserve.newWaiting(
                ticketId,
                param.getEncId(),
                param.getSessionKey(),
                now
        );

        hospitalReserveRepository.save(hospitalReserve);

        //ZSET ADD
        //ws joined

        return ticketId;
    }

    /**
     * 예약 인원만큼 슬롯을 개방하여, 대기열의 인원이 예약시스템에 참여할 수 있도록 처리
     *
     * @return
     */
    @Transactional
    @Override
    public List<UUID> openSlots(OpenSlotsParam param) {
        if (param.getSlotDelta() <= 0) return null;

        HospitalSlotEvent event = HospitalSlotEvent.of(
                param.getEncId(),
                HospitalSlotEventType.RESERVATION_CONSUMED,
                param.getSlotDelta()
        );

        hospitalSlotEventRepository.save(event);

        // WAITING 앞에서 slotDelta만큼 선발
        List<HospitalReserve> waitingList = hospitalReserveRepository.findFrontByStatusForUpdate(
                param.getEncId(),
                HospitalReserveStatus.WAITING,
                PageRequest.of(0, event.getSlotDelta())
        );

        LocalDateTime now = LocalDateTime.now();
        for (HospitalReserve reserve : waitingList) {
            reserve.markReady(
                    now,
                    now.plusMinutes(READY_EXPIRE_MINUTES)
            );
        }

        //ZSET에서 ZREM
        //WS READY 알림

        return waitingList.stream()
                .map(HospitalReserve::getTicketId)
                .toList();
    }

    /**
     * 지정된 시간만큼 예약을 진행하지 않을경우, 대기열의 인원이 예약을 진행할 수 있도록 슬롯을 반환
     */
    @Override
    public void expireReady(ExpireReadyParam param) {
        LocalDateTime now = LocalDateTime.now();

        List<HospitalReserve> expiredList = hospitalReserveRepository.findExpiredReadyCandidates(
                param.getEncId(),
                now,
                PageRequest.of(0, 50)
        );

        if(expiredList.isEmpty()) return;

        for (HospitalReserve reserve : expiredList) {
            reserve.markExpired(now);
        }

        HospitalSlotEvent hospitalSlotEvent = HospitalSlotEvent.of(
                param.getEncId(),
                HospitalSlotEventType.SLOT_RELEASED,
                expiredList.size()
        );
        hospitalSlotEventRepository.save(hospitalSlotEvent);
    }


}
