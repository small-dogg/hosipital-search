package com.smalldogg.hospitalsearch.queue;

import com.smalldogg.hospitalsearch.queue.entity.HospitalSlotEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalSlotEventRepository extends JpaRepository<HospitalSlotEvent, Long> {

    Optional<HospitalSlotEvent> findByEncIdAndIdempotencyKey(String encId, String idempotencyKey);

    boolean existsByEncIdAndIdempotencyKey(String encId, String idempotencyKey);
}

