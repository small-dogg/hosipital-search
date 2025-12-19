package com.smalldogg.hospitalsearch.config.warmup;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class WarmupState {

    public enum Phase { IN_PROGRESS, UP, DOWN }

    private final AtomicReference<Phase> phase = new AtomicReference<>(Phase.IN_PROGRESS);
    private final AtomicReference<Throwable> lastError = new AtomicReference<>(null);

    public Phase phase() { return phase.get(); }
    public Throwable lastError() { return lastError.get(); }

    public void markUp() {
        lastError.set(null);
        phase.set(Phase.UP);
    }

    public void markDown(Throwable t) {
        lastError.set(t);
        phase.set(Phase.DOWN);
    }
}

