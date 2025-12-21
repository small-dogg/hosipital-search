package com.smalldogg.hospitalsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HospitalSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalSearchApplication.class, args);
    }

}
