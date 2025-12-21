package com.smalldogg.hospitalsearch.queue;


import com.smalldogg.hospitalsearch.queue.in.ExpireReadyParam;
import com.smalldogg.hospitalsearch.queue.in.OpenSlotsParam;
import com.smalldogg.hospitalsearch.queue.in.ReserveHospitalParam;

import java.util.List;
import java.util.UUID;

public interface HospitalReserveService {

    UUID join(ReserveHospitalParam param);

    List<UUID> openSlots(OpenSlotsParam param);

    void expireReady(ExpireReadyParam param);
}
