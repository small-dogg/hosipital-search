package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, String> {
}
