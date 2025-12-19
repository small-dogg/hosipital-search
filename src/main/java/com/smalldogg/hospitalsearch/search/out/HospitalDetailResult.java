package com.smalldogg.hospitalsearch.search.out;

import com.smalldogg.hospitalsearch.search.entity.Hospital;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Value
public class HospitalDetailResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    String name;
    Integer typeCode;
    String typeName;
    Integer sidoCode;
    String sidoName;
    Integer sigunguCode;
    String sigunguName;
    String eupmyeondong;
    Integer zip;
    String address;
    String phone;
    String homepage;
    LocalDate openDate;
    Integer totalDoctor;
    Double x;
    Double y;

    public static HospitalDetailResult from(Hospital hospital) {
        if (hospital == null) {
            throw new IllegalArgumentException("hospital must not be null");
        }

        return new HospitalDetailResult(
                hospital.getName(),
                hospital.getTypeCode(),
                hospital.getTypeName(),
                hospital.getSidoCode(),
                hospital.getSidoName(),
                hospital.getSigunguCode(),
                hospital.getSigunguName(),
                hospital.getEupmyeondong(),
                hospital.getZip(),
                hospital.getAddress(),
                hospital.getPhone(),
                hospital.getHomepage(),
                hospital.getOpenDate(),
                hospital.getTotalDoctor(),
                hospital.getX(),
                hospital.getY()
        );
    }
}
