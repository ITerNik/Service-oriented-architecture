package ru.ifmo.front.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.front.client.RouteServiceSoapClient;
import ru.ifmo.front.model.DistanceResponse;

@RestController
@RequestMapping("api/route")
public class RouteProxyController {

    private static final Logger logger = LoggerFactory.getLogger(RouteProxyController.class);

    @Autowired
    private RouteServiceSoapClient routeServiceSoapClient;

    @GetMapping("/calculate/to-max-populated")
    public ResponseEntity<DistanceResponse> calculateToMaxPopulated() {
        try {
            Double distance = routeServiceSoapClient.calculateToMaxPopulated();
            return ResponseEntity.ok(new DistanceResponse(distance));
        } catch (Exception e) {
            logger.error("Error calculating distance to max populated city", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/calculate/between-oldest-and-newest")
    public ResponseEntity<DistanceResponse> calculateBetweenOldestAndNewest() {
        try {
            Double distance = routeServiceSoapClient.calculateBetweenOldestAndNewest();
            return ResponseEntity.ok(new DistanceResponse(distance));
        } catch (Exception e) {
            logger.error("Error calculating distance between oldest and newest cities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
