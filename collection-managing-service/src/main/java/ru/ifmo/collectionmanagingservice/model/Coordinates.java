package ru.ifmo.collectionmanagingservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"x", "y"})
public class Coordinates {
    @XmlElement(required = true)
    @NotNull
    private Double x;

    @XmlElement(required = true)
    @NotNull
    @Min(-194)
    private Double y;
}
