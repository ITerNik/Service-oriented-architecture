package ru.ifmo.front.model;

public class DistanceResponse {
    private Double distance;

    public DistanceResponse() {
    }

    public DistanceResponse(Double distance) {
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
