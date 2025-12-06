package com.smalldogg.hospitalsearch.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.command.SearchHospitalResultCommand;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
import com.smalldogg.hospitalsearch.search.out.SearchHospitalResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient esClient;


    @Override
    public List<SearchHospitalResult> searchHospitalResult(SearchHospitalResultCommand command) throws IOException {
        SearchResponse<SearchHospitalResult> result = esClient.search(s -> s
                .index("hospitals")
                .size(10)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .match(mq -> mq
                                                .field("word")
                                                .query(command.getKeyword())
                                        )
                                )
                        )
                ), SearchHospitalResult.class
        );

        return result.hits().hits().stream()
                .map(h -> h.source())
                .toList();
    }

    @Override
    public List<AutoCompleteHospitalResult> getAutoCompleteHospitals(GetAutoCompleteHospitalsCommand command) throws IOException {
        log.info("입력된 문자열 : {}", command.getKeyword());
        SearchResponse<AutoCompleteHospitalResult> result = esClient.search(s -> s
                .index("hospitals")
                .size(10)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .match(mq -> mq
                                                .field("word")
                                                .query(command.getKeyword())
                                        )
                                )
                        )
                ), AutoCompleteHospitalResult.class
        );

        return result.hits().hits().stream()
                .map(h -> h.source())
                .toList();
    }
}
