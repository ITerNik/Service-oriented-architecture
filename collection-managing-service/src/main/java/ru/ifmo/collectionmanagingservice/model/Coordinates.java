package ru.ifmo.collectionmanagingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Coordinates {
    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    private Double x;

    @NotNull
    @Min(-194)
    @Column(name = "coordinate_y", nullable = false)
    private Double y;

    public Coordinates() {}

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
}
