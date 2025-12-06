package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<AutoCompleteHospitalResult> getAutoCompleteHospitals(GetAutoCompleteHospitalsCommand command) throws IOException;
}
