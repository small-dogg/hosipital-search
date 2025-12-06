package com.smalldogg.hospitalsearch.search.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchHospitalResult {
    private String name;
    private String address;
    private Integer zip;
    private String phone;
    private LocationResult location;
}

@Getter
@NoArgsConstructor
class LocationResult {
        private float lat;
        private float lon;
}
