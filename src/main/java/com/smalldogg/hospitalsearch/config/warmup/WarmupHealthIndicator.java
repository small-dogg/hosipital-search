package com.smalldogg.hospitalsearch.config.warmup;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WarmupHealthIndicator implements HealthIndicator {
    private final WarmupState warmupState;

    @Override
    public Health health() {
        return switch (warmupState.phase()) {
            case UP -> Health.up()
                    .withDetail("warmup", "completed")
                    .build();

            case IN_PROGRESS -> Health.outOfService()
                    .withDetail("warmup", "in-progress")
                    .build();

            case DOWN -> {
                Throwable t = warmupState.lastError();
                yield Health.down()
                        .withDetail("warmup", "failed")
                        .withException(t)
                        .build();
            }
        };
    }
}
