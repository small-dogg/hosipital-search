package com.smalldogg.hospitalsearch.queue.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReserveHospitalParam {
    private String encId;
    private String sessionKey;
}
