package ru.ifmo.calculatingservice.config;

import com.netflix.concurrency.limits.Limiter;
import com.netflix.concurrency.limits.limit.Gradient2Limit;
import com.netflix.concurrency.limits.limit.VegasLimit;
import com.netflix.concurrency.limits.limiter.SimpleLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrencyLimitsConfig {
    private Logger log = LoggerFactory.getLogger(ConcurrencyLimitsConfig.class);

    @Bean
    public Limiter<String> concurrencyLimiter() {
        log.info("Initializing Gradient2Limit concurrency limiter");

        Gradient2Limit limit = Gradient2Limit.newBuilder()
                .initialLimit(10)
                .minLimit(5)
                .maxConcurrency(20)
                .smoothing(0.2)
                .rttTolerance(1.5)
                .build();

        Limiter<String> limiter = SimpleLimiter.newBuilder()
                .named("route-service-limiter")
                .limit(limit)
                .build();

        log.info("Gradient2Limit limiter created with initial limit: 10, max: 20");
        return limiter;
    }

    @Bean(name = "vegasLimiter")
    public Limiter<String> vegasLimiter() {
        log.info("Initializing VegasLimit concurrency limiter");

        VegasLimit limit = VegasLimit.newBuilder()
                .initialLimit(10)
                .maxConcurrency(20)
                .alpha(3)
                .beta(6)
                .smoothing(1.0)
                .build();

        Limiter<String> limiter = SimpleLimiter.newBuilder()
                .named("route-service-vegas")
                .limit(limit)
                .build();

        log.info("VegasLimit limiter created with initial limit: 10, max: 20");
        return limiter;
    }
}
