package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
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

    @RequestMapping("/autocomplete/{keyword}")
    @GetMapping
    public List<AutoCompleteHospitalResult> getAutoCompleteHospitals(@PathVariable String keyword) throws IOException {
        return searchService.getAutoCompleteHospitals(
                new GetAutoCompleteHospitalsCommand(keyword)
        );
    }
}
