package ru.ifmo.calculatingservice.service;

import com.netflix.concurrency.limits.Limiter;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Supplier;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote;
import ru.ifmo.calculatingservice.exception.ServiceOverloadedException;

@Service
public class RouteService {

    @Value("${collection-managing-service.url}")
    private String service1Url;

    @Autowired
    private Limiter<String> concurrencyLimiter;

    private RouteCalculatorRemote routeCalculator;

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            routeCalculator = (RouteCalculatorRemote)
                    ctx.lookup(
                            "java:global/calculating-service/calculating-service-ejb/RouteCalculatorBean!ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup EJB", e);
        }
    }

    public double calculateToMaxPopulated() {
        return executeWithLimit("calculateToMaxPopulated", () -> routeCalculator.calculateToMaxPopulated(service1Url));
    }

    public double calculateBetweenOldestAndNewest() {
        return executeWithLimit(
                "calculateBetweenOldestAndNewest", () -> routeCalculator.calculateBetweenOldestAndNewest(service1Url));
    }

    private <T> T executeWithLimit(String context, Supplier<T> operation) {
        Optional<Limiter.Listener> listener = concurrencyLimiter.acquire(context);

        if (!listener.isPresent()) {
            throw new ServiceOverloadedException("Service is overloaded, please try again later");
        }

        long startTime = System.nanoTime();
        try {
            T result = operation.get();
            listener.get().onSuccess();

            long duration = System.nanoTime() - startTime;

            return result;
        } catch (Exception e) {
            listener.get().onIgnore();
            throw e;
        }
    }
}
