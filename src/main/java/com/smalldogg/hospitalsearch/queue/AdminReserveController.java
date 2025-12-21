package com.smalldogg.hospitalsearch.queue;

import com.smalldogg.hospitalsearch.queue.entity.HospitalReserve;
import com.smalldogg.hospitalsearch.queue.in.OpenSlotsParam;
import com.smalldogg.hospitalsearch.queue.out.QueueStatusMessage;
import com.smalldogg.hospitalsearch.queue.websocket.QueuePushSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminReserveController {

    private final HospitalReserveService queueService;
    private final HospitalReserveRepository reserveRepository;
    private final QueuePushSender pushSender;

    /**
     * 테스트용: 슬롯 열기
     * POST /admin/hospitals/{encId}/open-slots?count=1
     */
    @PostMapping("/admin/hospitals/{encId}/open-slots")
    public void openSlots(@PathVariable String encId, @RequestParam(defaultValue = "1") int count) {
        List<UUID> ticketIds = queueService.openSlots(new OpenSlotsParam(encId, count));

        if(ticketIds.isEmpty()) return;

        // READY로 바뀐 앞쪽 N명을 찾아 push (TODO: READY를 최근에 만든 걸로 선별하는 개선은 다음 단계)
        for (UUID ticketId : ticketIds) {
            HospitalReserve r = reserveRepository.findByEncIdAndTicketId(encId, ticketId)
                    .orElseThrow();

            pushSender.sendToTicket(encId, ticketId,
                    QueueStatusMessage.builder()
                            .encId(encId)
                            .ticketId(ticketId)
                            .status(r.getStatus())
                            .position(null) // 다음 단계에서 WAITING 순번 주기 push로 채움
                            .readyDeadlineAt(r.getReadyDeadlineAt())
                            .enterUrl("/hospitals/" + encId + "/reserve?ticketId=" + ticketId)
                            .build()
            );
        }
    }
}
