package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.command.SearchHospitalResultCommand;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
import com.smalldogg.hospitalsearch.search.out.SearchHospitalResult;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<SearchHospitalResult> searchHospitalResult(SearchHospitalResultCommand command) throws IOException;

    List<AutoCompleteHospitalResult> getAutoCompleteHospitals(GetAutoCompleteHospitalsCommand command) throws IOException;
}
