package com.smalldogg.hospitalsearch.search;

import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.command.SearchHospitalResultCommand;
import com.smalldogg.hospitalsearch.search.in.HospitalBulkUpsertRequest;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
import com.smalldogg.hospitalsearch.search.out.HospitalDetailResult;
import com.smalldogg.hospitalsearch.search.out.SearchHospitalResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/detail/{encId}")
    @GetMapping
    public HospitalDetailResult getHospitalDetail(@PathVariable String encId) {
        return searchService.getHospitalDetail(encId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bulk")
    public void bulkUpsert(@RequestBody @Valid @Size(min = 1, max = 100) List<@Valid HospitalBulkUpsertRequest> requests) {
        searchService.bulkUpsert(requests); // 삽입 로직은 사용자 구현
    }

}
