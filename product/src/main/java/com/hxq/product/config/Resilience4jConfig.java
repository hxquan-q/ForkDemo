package com.hxq.product.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j断路器配置
 */
@Configuration
public class Resilience4jConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3)) // 超时时间
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50) // 失败率阈值
                        .waitDurationInOpenState(Duration.ofSeconds(10)) // 断路器开启时间
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 滑动窗口类型
                        .slidingWindowSize(10) // 滑动窗口大小
                        .minimumNumberOfCalls(5) // 最小调用次数
                        .permittedNumberOfCallsInHalfOpenState(3) // 半开状态允许的调用次数
                        .build())
                .build());
    }
}