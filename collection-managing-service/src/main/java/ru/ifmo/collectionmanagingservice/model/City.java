package ru.ifmo.collectionmanagingservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.Objects;

public class City {

    private ObjectId _id;

    private String id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Valid
    private Coordinates coordinates;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    @NotNull
    @Min(1)
    private Integer area;

    @NotNull
    @Min(1)
    private Integer population;

    private Integer metersAboveSeaLevel;

    private Boolean capital;

    private Float agglomeration;

    @NotNull
    private Climate climate;

    @NotNull
    @Valid
    private Human governor;

    public City() {
        if (creationDate == null) {
            creationDate = LocalDate.now();
        }
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        City city = (City) o;
        return Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
