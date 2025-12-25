package ru.ifmo.restadapter.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ifmo.restadapter.client.Web2SoapClient;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final Web2SoapClient web2SoapClient;

    @Autowired
    public RouteController(Web2SoapClient web2SoapClient) {
        this.web2SoapClient = web2SoapClient;
    }

    @GetMapping("/calculate/to-max-populated")
    public ResponseEntity<Map<String, Double>> calculateToMaxPopulated() {
        try {
            double distance = web2SoapClient.calculateToMaxPopulated();
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
            double distance = web2SoapClient.calculateBetweenOldestAndNewest();
            Map<String, Double> response = new HashMap<>();
            response.put("distance", distance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
