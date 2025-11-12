package ru.ifmo.calculatingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class City {
    private String id;
    private String name;
    private Coordinates coordinates;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private Integer area;
    private Integer population;
    private Integer metersAboveSeaLevel;
    private Boolean capital;
    private Float agglomeration;
    private Climate climate;
    private Human governor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    public void setMetersAboveSeaLevel(Integer meters) {
        this.metersAboveSeaLevel = meters;
    }

    public Boolean getCapital() {
        return capital;
    }

    public void setCapital(Boolean capital) {
        this.capital = capital;
    }

    public Float getAgglomeration() {
        return agglomeration;
    }

    public void setAgglomeration(Float agglomeration) {
        this.agglomeration = agglomeration;
    }

    public Climate getClimate() {
        return climate;
    }

    public void setClimate(Climate climate) {
        this.climate = climate;
    }

    public Human getGovernor() {
        return governor;
    }

    public void setGovernor(Human governor) {
        this.governor = governor;
    }
}
