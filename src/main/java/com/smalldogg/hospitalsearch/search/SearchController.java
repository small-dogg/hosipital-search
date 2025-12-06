package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.command.SearchHospitalResultCommand;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
import com.smalldogg.hospitalsearch.search.out.SearchHospitalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/search")
@RestController
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/{keyword}")
    public List<SearchHospitalResult> searchHospitalResultList(@PathVariable String keyword) throws IOException {
        return searchService.searchHospitalResult(
                new SearchHospitalResultCommand(keyword)
        );
    }

    @RequestMapping("/autocomplete/{keyword}")
    @GetMapping
    public List<AutoCompleteHospitalResult> getAutoCompleteHospitals(@PathVariable String keyword) throws IOException {
        return searchService.getAutoCompleteHospitals(
                new GetAutoCompleteHospitalsCommand(keyword)
        );
    }

}
