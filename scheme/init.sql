CREATE TABLE IF NOT EXISTS hospital_reserve
(
    -- 대기열 티켓 식별자
    ticket_id           uuid PRIMARY KEY,

    -- 병원 식별자 (encId)
    enc_id              varchar(80) NOT NULL,

    -- 사용자 대신 세션 식별자 (테스트 목적)
    session_key         varchar(80) NOT NULL,

    -- 상태 (WAITING / READY / CONSUMED / LEFT / EXPIRED)
    status              varchar(20) NOT NULL DEFAULT 'WAITING',

    -- 대기열 진입 시각
    joined_at           timestamp with time zone NOT NULL DEFAULT now(),

    -- READY 전환 시각 및 READY 유효 마감 시각
    ready_at            timestamp with time zone NULL,
    ready_deadline_at   timestamp with time zone NULL,

    -- 예약 완료(슬롯 소비) 시각
    consumed_at         timestamp with time zone NULL,

    -- 사용자 이탈 / READY 만료 시각
    left_at             timestamp with time zone NULL,
    expired_at          timestamp with time zone NULL,

                                      -- 확장 메타 데이터
                                      meta                jsonb NOT NULL DEFAULT '{}'::jsonb
                                      );

-- 같은 병원(enc_id)에서 동일 session_key의 활성 대기 중복 방지
-- (WAITING, READY 상태만 유니크)
CREATE UNIQUE INDEX IF NOT EXISTS hospital_reserve_uq_active_session
    ON hospital_reserve (enc_id, session_key)
    WHERE status IN ('WAITING', 'READY');

-- 병원별 대기열/READY 목록 조회 최적화
CREATE INDEX IF NOT EXISTS hospital_reserve_ix_enc_status_joined
    ON hospital_reserve (enc_id, status, joined_at);

-- enc_id + ticket_id 조합 조회 최적화
CREATE INDEX IF NOT EXISTS hospital_reserve_ix_enc_ticket
    ON hospital_reserve (enc_id, ticket_id);

ALTER TABLE hospital_reserve
    ADD CONSTRAINT hospital_reserve_status_ck
        CHECK (status IN ('WAITING','READY','CONSUMED','LEFT','EXPIRED'));

ALTER TABLE hospital_slot_event
    ADD CONSTRAINT hospital_slot_event_type_ck
        CHECK (event_type IN (
                              'RESERVATION_CONSUMED',
                              'SLOT_RELEASED',
                              'MANUAL_ADJUST',
                              'SYSTEM_RECONCILE'
            ));


CREATE TABLE IF NOT EXISTS hospital_slot_event
(
    id                 bigserial PRIMARY KEY,

    -- 병원 식별자
    enc_id              varchar(80) NOT NULL,

    -- 이벤트 타입
    -- (RESERVATION_CONSUMED / SLOT_RELEASED / MANUAL_ADJUST / SYSTEM_RECONCILE 등)
    event_type          varchar(40) NOT NULL,

    -- 슬롯 변화량 (+면 슬롯 생성/반환, -면 슬롯 소비)
    slot_delta          integer NOT NULL,

    -- 이벤트 발생 시각
    occurred_at         timestamp with time zone NOT NULL DEFAULT now(),

    -- 멱등 처리용 키 (외부 예약 ID 등)
    idempotency_key     varchar(120) NULL,

    -- 관련된 대기열 티켓
    related_ticket_id   uuid NULL,

    -- 이벤트 상세 정보
    payload             jsonb NOT NULL DEFAULT '{}'::jsonb
    );

-- 병원별 이벤트 시간순 리플레이
CREATE INDEX IF NOT EXISTS hospital_slot_event_ix_enc_occurred
    ON hospital_slot_event (enc_id, occurred_at);

-- 멱등 키가 있는 경우만 유니크 보장
CREATE UNIQUE INDEX IF NOT EXISTS hospital_slot_event_uq_idempotency
    ON hospital_slot_event (enc_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;

-- 티켓 기준 이벤트 추적
CREATE INDEX IF NOT EXISTS hospital_slot_event_ix_related_ticket
    ON hospital_slot_event (related_ticket_id);
