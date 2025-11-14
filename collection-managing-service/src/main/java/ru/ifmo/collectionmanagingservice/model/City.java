package ru.ifmo.collectionmanagingservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@XmlRootElement(name = "city")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "name", "coordinates", "creationDate", "area", "population",
        "metersAboveSeaLevel", "capital", "agglomeration", "climate", "governor"})
public class City {

    @XmlElement
    @Min(1)
    private String id;

    @XmlElement(required = true)
    @NotNull
    @NotEmpty
    private String name;

    @XmlElement(required = true)
    @NotNull
    @Valid
    private Coordinates coordinates;

    @XmlElement
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    @XmlElement(required = true)
    @NotNull
    @Min(1)
    private Integer area;

    @XmlElement(required = true)
    @NotNull
    @Min(1)
    private Integer population;

    @XmlElement
    private Integer metersAboveSeaLevel;

    @XmlElement
    private Boolean capital;

    @XmlElement
    private Float agglomeration;

    @XmlElement(required = true)
    @NotNull
    private Climate climate;

    @XmlElement(required = true)
    @NotNull
    @Valid
    private Human governor;
}