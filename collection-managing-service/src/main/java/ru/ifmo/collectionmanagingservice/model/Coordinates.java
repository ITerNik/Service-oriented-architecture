package ru.ifmo.collectionmanagingservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Coordinates {
    @NotNull
    private Double x;

    @NotNull
    @Min(-194)
    private Double y;

    public Coordinates() {
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
