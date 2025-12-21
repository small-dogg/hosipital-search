package com.smalldogg.hospitalsearch.queue.entity;

import com.smalldogg.hospitalsearch.config.jpa.JsonbStringConverter;
import com.smalldogg.hospitalsearch.queue.enums.HospitalSlotEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hospital_slot_event",
        indexes = {
                @Index(name = "hospital_slot_event_ix_enc_occurred", columnList = "enc_id,occurred_at"),
                @Index(name = "hospital_slot_event_ix_related_ticket", columnList = "related_ticket_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class HospitalSlotEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enc_id", length = 80, nullable = false)
    private String encId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 40, nullable = false)
    private HospitalSlotEventType eventType;

    @Column(name = "slot_delta", nullable = false)
    private int slotDelta;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "idempotency_key", length = 120)
    private String idempotencyKey;

    @Column(name = "related_ticket_id")
    private java.util.UUID relatedTicketId;

    @ColumnTransformer(write = "?::jsonb")
    @Convert(converter = JsonbStringConverter.class)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    public static HospitalSlotEvent of(String encId,
                                       HospitalSlotEventType type,
                                       int delta) {
        return HospitalSlotEvent.builder()
                .encId(encId)
                .eventType(type)
                .slotDelta(delta)
                .occurredAt(LocalDateTime.now())
                .idempotencyKey(null)
                .relatedTicketId(null)
                .payload(null)
                .build();
    }
}
