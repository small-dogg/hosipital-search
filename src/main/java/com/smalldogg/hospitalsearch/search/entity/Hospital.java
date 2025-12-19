package com.smalldogg.hospitalsearch.search.entity;

import com.smalldogg.hospitalsearch.search.in.HospitalBulkUpsertRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "hospital")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @Column(name = "enc_id", length = 80, nullable = false, updatable = false)
    private String encId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "type_code", nullable = false)
    private Integer typeCode;

    @Column(name = "type_name", length = 30, nullable = false)
    private String typeName;

    @Column(name = "sido_code", nullable = false)
    private Integer sidoCode;

    @Column(name = "sido_name", length = 20, nullable = false)
    private String sidoName;

    @Column(name = "sigungu_code", nullable = false)
    private Integer sigunguCode;

    @Column(name = "sigungu_name", length = 20, nullable = false)
    private String sigunguName;

    @Column(name = "eupmyeondong", length = 20, nullable = false)
    private String eupmyeondong;

    @Column(name = "zip", nullable = false)
    private Integer zip;

    @Column(name = "address", length = 256, nullable = false)
    private String address;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "homepage", length = 512)
    private String homepage;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "total_doctor", nullable = false)
    private Integer totalDoctor;

    @Column(name = "x", columnDefinition = "double precision")
    private Double x;

    @Column(name = "y", columnDefinition = "double precision")
    private Double y;

    public static Hospital of(HospitalBulkUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        return Hospital.builder()
                .encId(request.getEncId())
                .name(request.getName())
                .typeCode(request.getTypeCode())
                .typeName(request.getTypeName())
                .sidoCode(request.getSidoCode())
                .sidoName(request.getSidoName())
                .sigunguCode(request.getSigunguCode())
                .sigunguName(request.getSigunguName())
                .eupmyeondong(request.getEupmyeondong())
                .zip(request.getZip())
                .address(request.getAddress())
                .phone(request.getPhone())
                .homepage(request.getHomepage())
                .openDate(request.getOpenDate())
                .totalDoctor(request.getTotalDoctor())
                .x(request.getX())
                .y(request.getY())
                .build();
    }

}
