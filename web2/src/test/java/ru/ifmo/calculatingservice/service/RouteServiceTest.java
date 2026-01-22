package ru.ifmo.calculatingservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.concurrency.limits.Limiter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote;
import ru.ifmo.calculatingservice.exception.ServiceOverloadedException;

public class RouteServiceTest {

    private RouteService routeService;

    @Mock
    private RouteCalculatorRemote routeCalculator;

    @Mock
    private Limiter<String> concurrencyLimiter;

    @Mock
    private Limiter.Listener listener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        routeService = new RouteService();

        ReflectionTestUtils.setField(routeService, "routeCalculator", routeCalculator);
        ReflectionTestUtils.setField(routeService, "service1Url", "http://localhost:8080");
        ReflectionTestUtils.setField(routeService, "concurrencyLimiter", concurrencyLimiter);

        when(concurrencyLimiter.acquire(anyString())).thenReturn(Optional.of(listener));
    }

    @Test
    public void testCalculateToMaxPopulated() {
        double expectedDistance = 50.0;
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenReturn(expectedDistance);

        double distance = routeService.calculateToMaxPopulated();

        assertEquals(expectedDistance, distance);
        verify(listener).onSuccess();
        verify(concurrencyLimiter).acquire("calculateToMaxPopulated");
    }

    @Test
    public void testCalculateToMaxPopulatedZero() {
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenReturn(0.0);

        double distance = routeService.calculateToMaxPopulated();

        assertEquals(0.0, distance);
        verify(listener).onSuccess();
    }

    @Test
    public void testCalculateBetweenOldestAndNewest() {
        double expectedDistance = 100.0;
        when(routeCalculator.calculateBetweenOldestAndNewest(anyString())).thenReturn(expectedDistance);

        double distance = routeService.calculateBetweenOldestAndNewest();

        assertEquals(expectedDistance, distance);
        verify(listener).onSuccess();
        verify(concurrencyLimiter).acquire("calculateBetweenOldestAndNewest");
    }

    @Test
    public void testCalculateBetweenOldestAndNewestZero() {
        when(routeCalculator.calculateBetweenOldestAndNewest(anyString())).thenReturn(0.0);

        double distance = routeService.calculateBetweenOldestAndNewest();

        assertEquals(0.0, distance);
        verify(listener).onSuccess();
    }

    @Test
    public void testConcurrencyLimitExceeded() {
        when(concurrencyLimiter.acquire(anyString())).thenReturn(Optional.empty());

        assertThrows(ServiceOverloadedException.class, () -> {
            routeService.calculateToMaxPopulated();
        });

        verify(routeCalculator, never()).calculateToMaxPopulated(anyString());
    }

    @Test
    public void testCalculateToMaxPopulatedWithException() {
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenThrow(new RuntimeException("EJB error"));

        assertThrows(RuntimeException.class, () -> {
            routeService.calculateToMaxPopulated();
        });

        verify(listener).onIgnore();
    }

    @Test
    public void testCalculateBetweenOldestAndNewestWithException() {
        when(routeCalculator.calculateBetweenOldestAndNewest(anyString())).thenThrow(new RuntimeException("EJB error"));

        assertThrows(RuntimeException.class, () -> {
            routeService.calculateBetweenOldestAndNewest();
        });

        verify(listener).onIgnore();
    }

    @Test
    public void testMultipleConcurrentRequests() {
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenReturn(50.0);

        for (int i = 0; i < 5; i++) {
            double distance = routeService.calculateToMaxPopulated();
            assertEquals(50.0, distance);
        }

        verify(listener, times(5)).onSuccess();
        verify(concurrencyLimiter, times(5)).acquire("calculateToMaxPopulated");
    }
}
