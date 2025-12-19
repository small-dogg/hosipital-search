package com.smalldogg.hospitalsearch.config.warmup;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.smalldogg.hospitalsearch.config.annotation.Warmup;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class WarmupConfig {

    private final RedissonClient redissonClient;
    private final ElasticsearchClient elasticsearchClient;

    @Warmup
    public void initializeRedisson() {
        String key = "warmup:redission:ping";
        RBucket<String> bucket = redissonClient.getBucket(key);

        bucket.set("okay", 10, TimeUnit.SECONDS);

        bucket.get();

    }
    @Warmup
    public void initializeElasticsearch() throws IOException {
        elasticsearchClient.search(s ->
                s.index("hospitals")
                .size(0)
                .query(q -> q.matchAll(m -> m)),Void.class
        );
    }
}
