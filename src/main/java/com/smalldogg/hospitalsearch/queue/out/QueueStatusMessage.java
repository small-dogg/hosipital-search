package com.smalldogg.hospitalsearch.queue.out;

import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class QueueStatusMessage {
    String encId;
    UUID ticketId;
    HospitalReserveStatus status;
    Integer position;
    LocalDateTime readyDeadlineAt;
    String enterUrl;
}
