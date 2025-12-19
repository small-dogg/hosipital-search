package com.smalldogg.hospitalsearch.search.in;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalBulkUpsertRequest {

    private String encId;

    private String name;

    private Integer typeCode;

    private String typeName;

    private Integer sidoCode;

    private String sidoName;

    private Integer sigunguCode;

    private String sigunguName;

    private String eupmyeondong;

    private Integer zip;

    private String address;

    private String phone;

    private String homepage;

    private LocalDate openDate;

    private Integer totalDoctor;

    private Double x;
    private Double y;
}

