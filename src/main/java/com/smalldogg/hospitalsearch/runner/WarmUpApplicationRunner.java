package com.smalldogg.hospitalsearch.runner;

import com.smalldogg.hospitalsearch.config.annotation.Warmup;
import com.smalldogg.hospitalsearch.config.warmup.WarmupState;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class WarmUpApplicationRunner implements ApplicationRunner {

    private final ApplicationContext applicationContext;
    private final WarmupState warmupState;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            String[] beanNames = applicationContext.getBeanDefinitionNames();

            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);

                Class<?> targetClass = AopUtils.getTargetClass(bean);

                Arrays.stream(targetClass.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Warmup.class))
                        .forEach(method -> invokeWarmupMethod(bean, method));
            }
            warmupState.markUp();
        }catch (Exception e){
            warmupState.markDown(e);
            throw e;
        }
    }

    private void invokeWarmupMethod(Object bean, Method method) {
        try {
            method.setAccessible(true);

            if (method.getParameterCount() > 0) {
                throw new IllegalStateException(
                        "@Warmup method must have no parameters: " + method
                );
            }

            method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException("Warmup method failed: " + method, e);
        }
    }

}
