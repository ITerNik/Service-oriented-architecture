package ru.ifmo.collectionmanagingservice.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ifmo.collectionmanagingservice.model.City;
import java.util.List;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({GetCitiesResponse.class, CreateCityResponse.class, GetCityResponse.class,
        UpdateCityResponse.class, DeleteCityResponse.class})
public abstract class BaseResponse {
    @XmlElement(required = true)
    private boolean success;

    @XmlElement
    private int statusCode;

    @XmlElement
    private String errorMessage;
}

