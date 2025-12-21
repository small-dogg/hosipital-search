package com.smalldogg.hospitalsearch.queue.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenSlotsParam {
    private String encId;
    private Integer slotDelta;
}
