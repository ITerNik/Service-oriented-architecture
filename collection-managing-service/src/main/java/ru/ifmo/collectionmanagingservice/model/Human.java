package ru.ifmo.collectionmanagingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "human")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"height", "birthday"})
public class Human {

    @XmlElement(required = true)
    @NotNull
    @Min(1)
    private Double height;

    @XmlElement
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    public Human(Double height) {
        this.height = height;
    }
}
