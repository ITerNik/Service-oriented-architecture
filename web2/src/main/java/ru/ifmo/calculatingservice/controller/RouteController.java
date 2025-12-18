package ru.ifmo.calculatingservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.calculatingservice.service.RouteService;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/calculate/to-max-populated")
    public ResponseEntity<Map<String, Double>> calculateToMaxPopulated() {
        try {
            double distance = routeService.calculateToMaxPopulated();
            Map<String, Double> response = new HashMap<>();
            response.put("distance", distance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/calculate/between-oldest-and-newest")
    public ResponseEntity<Map<String, Double>> calculateBetweenOldestAndNewest() {
        try {
            double distance = routeService.calculateBetweenOldestAndNewest();
            Map<String, Double> response = new HashMap<>();
            response.put("distance", distance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
