package com.smalldogg.hospitalsearch.config.aop.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    // 락 이름
    String key();

    // 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    //대기 시간 - 락 획득을 위해 waitTime 만큼 대기
    long waitTime() default 3L;

    // 락 임대 시간 - 획득 후 leaseTime이 지나면 락 해제.
    long leaseTime() default 2L;
}
