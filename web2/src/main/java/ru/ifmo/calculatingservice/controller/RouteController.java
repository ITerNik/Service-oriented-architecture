package ru.ifmo.calculatingservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.calculatingservice.exception.ServiceOverloadedException;
import ru.ifmo.calculatingservice.service.RouteService;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/calculate/to-max-populated")
    public ResponseEntity<Map<String, Object>> calculateToMaxPopulated() {
        try {
            double distance = routeService.calculateToMaxPopulated();
            Map<String, Object> response = new HashMap<>();
            response.put("distance", distance);
            return ResponseEntity.ok(response);
        } catch (ServiceOverloadedException e) {
            return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @GetMapping("/calculate/between-oldest-and-newest")
    public ResponseEntity<Map<String, Object>> calculateBetweenOldestAndNewest() {
        try {
            double distance = routeService.calculateBetweenOldestAndNewest();
            Map<String, Object> response = new HashMap<>();
            response.put("distance", distance);
            return ResponseEntity.ok(response);
        } catch (ServiceOverloadedException e) {
            return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("status", status.value());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ServiceOverloadedException.class)
    public ResponseEntity<Map<String, Object>> handleOverload(ServiceOverloadedException e) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }
}
