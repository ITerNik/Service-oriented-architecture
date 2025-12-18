package ru.ifmo.calculatingservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote;

public class RouteServiceTest {

    private RouteService routeService;
    private RouteCalculatorRemote routeCalculator;

    @BeforeEach
    public void setUp() {
        routeService = new RouteService();
        routeCalculator = Mockito.mock(RouteCalculatorRemote.class);

        // Inject mocked EJB and URL
        ReflectionTestUtils.setField(routeService, "routeCalculator", routeCalculator);
        ReflectionTestUtils.setField(routeService, "service1Url", "http://localhost:8080");
    }

    @Test
    public void testCalculateToMaxPopulated() {
        double expectedDistance = 50.0;
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenReturn(expectedDistance);

        double distance = routeService.calculateToMaxPopulated();

        assertEquals(expectedDistance, distance);
    }

    @Test
    public void testCalculateToMaxPopulatedZero() {
        when(routeCalculator.calculateToMaxPopulated(anyString())).thenReturn(0.0);

        double distance = routeService.calculateToMaxPopulated();

        assertEquals(0.0, distance);
    }

    @Test
    public void testCalculateBetweenOldestAndNewest() {
        double expectedDistance = 100.0;
        when(routeCalculator.calculateBetweenOldestAndNewest(anyString())).thenReturn(expectedDistance);

        double distance = routeService.calculateBetweenOldestAndNewest();

        assertEquals(expectedDistance, distance);
    }

    @Test
    public void testCalculateBetweenOldestAndNewestZero() {
        when(routeCalculator.calculateBetweenOldestAndNewest(anyString())).thenReturn(0.0);

        double distance = routeService.calculateBetweenOldestAndNewest();

        assertEquals(0.0, distance);
    }
}
