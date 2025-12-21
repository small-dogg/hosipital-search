package com.smalldogg.hospitalsearch.queue;

import com.smalldogg.hospitalsearch.queue.entity.HospitalReserve;
import com.smalldogg.hospitalsearch.queue.enums.HospitalReserveStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HospitalReserveRepository extends JpaRepository<HospitalReserve, UUID> {

    Optional<HospitalReserve> findByTicketId(UUID ticketId);

    Optional<HospitalReserve> findByEncIdAndTicketId(String encId, UUID ticketId);

    boolean existsByEncIdAndSessionKeyAndStatusIn(String encId, String sessionKey, List<HospitalReserveStatus> statuses);

    Optional<HospitalReserve> findFirstByEncIdAndSessionKeyAndStatusIn(String encId, String sessionKey, List<HospitalReserveStatus> statuses);

    /**
     * WAITING 중 joined_at 순으로 앞에서 N명 조회 (READY 승격 대상)
     * - pageable size로 N을 제한
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select r
            from HospitalReserve r
            where r.encId = :encId
              and r.status = :status
            order by r.joinedAt asc
            """)
    List<HospitalReserve> findFrontByStatusForUpdate(@Param("encId") String encId,
                                                     @Param("status") HospitalReserveStatus status,
                                                     Pageable pageable);

    /**
     * READY 만료 처리 대상 조회
     */
    @Query("""
            select r
            from HospitalReserve r
            where r.encId = :encId
              and r.status = 'READY'
              and r.readyDeadlineAt <= :now
            order by r.readyDeadlineAt asc
            """)
    List<HospitalReserve> findExpiredReadyCandidates(@Param("encId") String encId,
                                                     @Param("now") LocalDateTime now,
                                                     Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update HospitalReserve r
               set r.status = :toStatus
             where r.encId = :encId
               and r.status = :fromStatus
               and r.ticketId in :ticketIds
            """)
    int bulkUpdateStatus(@Param("encId") String encId,
                         @Param("fromStatus") HospitalReserveStatus fromStatus,
                         @Param("toStatus") HospitalReserveStatus toStatus,
                         @Param("ticketIds") List<UUID> ticketIds);
}
