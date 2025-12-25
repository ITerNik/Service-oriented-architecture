package ru.ifmo.muleesb.model;

import jakarta.xml.bind.annotation.*;
import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "City",
        propOrder = {
            "id",
            "name",
            "coordinates",
            "creationDate",
            "area",
            "population",
            "metersAboveSeaLevel",
            "capital",
            "agglomeration",
            "climate",
            "governor"
        })
@XmlRootElement(name = "city")
public class City {

    private Long id;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private Coordinates coordinates;

    @XmlElement(required = true)
    private LocalDate creationDate;

    @XmlElement(required = true)
    private Integer area;

    @XmlElement(required = true)
    private Integer population;

    private Integer metersAboveSeaLevel;

    private Boolean capital;

    private Float agglomeration;

    @XmlElement(required = true)
    private Climate climate;

    @XmlElement(required = true)
    private Human governor;

    public City() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setMetersAboveSeaLevel(Integer metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
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
