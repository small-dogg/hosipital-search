package com.smalldogg.hospitalsearch.queue.entity;

import com.smalldogg.hospitalsearch.config.jpa.JsonbStringConverter;
import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(
        name = "hospital_reserve",
        indexes = {
                @Index(name = "hospital_reserve_ix_enc_status_joined", columnList = "enc_id,status,joined_at"),
                @Index(name = "hospital_reserve_ix_enc_ticket", columnList = "enc_id,ticket_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class HospitalReserve {

    @Id
    @Column(name = "ticket_id", nullable = false, updatable = false)
    private UUID ticketId;

    @Column(name = "enc_id", length = 80, nullable = false)
    private String encId;

    @Column(name = "session_key", length = 80, nullable = false)
    private String sessionKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private HospitalReserveStatus status;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "ready_at")
    private LocalDateTime readyAt;

    @Column(name = "ready_deadline_at")
    private LocalDateTime readyDeadlineAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonbStringConverter.class)
    @Column(name = "meta", nullable = false, columnDefinition = "jsonb")
    private String meta;

    public static HospitalReserve newWaiting(UUID ticketId, String encId, String sessionKey, LocalDateTime now) {
        return HospitalReserve.builder()
                .ticketId(ticketId)
                .encId(encId)
                .sessionKey(sessionKey)
                .status(HospitalReserveStatus.WAITING)
                .joinedAt(now)
                .meta("{}")
                .build();
    }

    public void markReady(LocalDateTime now, LocalDateTime deadline) {
        this.status = HospitalReserveStatus.READY;
        this.readyAt = now;
        this.readyDeadlineAt = deadline;
    }

    public void markConsumed(LocalDateTime now) {
        this.status = HospitalReserveStatus.CONSUMED;
        this.consumedAt = now;
    }

    public void markLeft(LocalDateTime now) {
        this.status = HospitalReserveStatus.LEFT;
        this.leftAt = now;
    }

    public void markExpired(LocalDateTime now) {
        this.status = HospitalReserveStatus.EXPIRED;
        this.expiredAt = now;
    }

    public double getJoinedAtToEpoch() {
        long epochMilli = joinedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return epochMilli + (Math.abs(ticketId.hashCode()) % 1000) / 1000.0;
    }
}
