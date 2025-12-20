package com.smalldogg.hospitalsearch.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.smalldogg.hospitalsearch.config.aop.lock.DistributedLock;
import com.smalldogg.hospitalsearch.search.command.GetAutoCompleteHospitalsCommand;
import com.smalldogg.hospitalsearch.search.command.SearchHospitalResultCommand;
import com.smalldogg.hospitalsearch.search.entity.Hospital;
import com.smalldogg.hospitalsearch.search.in.HospitalBulkUpsertRequest;
import com.smalldogg.hospitalsearch.search.out.AutoCompleteHospitalResult;
import com.smalldogg.hospitalsearch.search.out.HospitalDetailResult;
import com.smalldogg.hospitalsearch.search.out.SearchHospitalResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final HospitalRepository hospitalRepository;
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

//    @DistributedLock(name="get-auto-complete-lock", delay =  5000L)
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

    @DistributedLock(key="detail")
    @Cacheable(cacheNames = "hospital:detail", key="#encId")
    @Transactional
    @Override
    public HospitalDetailResult getHospitalDetail(String encId) {
        Hospital hospital = hospitalRepository.findById(encId)
                .orElseThrow(() -> new IllegalArgumentException("병원 없어요"));
        return HospitalDetailResult.from(hospital);
    }

    @Transactional
    @Override
    public void bulkUpsert(List<HospitalBulkUpsertRequest> requests) {
        List<Hospital> hospitals = new ArrayList<>();
        for (HospitalBulkUpsertRequest request : requests) {
            hospitals.add(
                    Hospital.of(request)
            );
        }
        hospitalRepository.saveAll(hospitals);
    }
}
