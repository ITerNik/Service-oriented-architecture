package ru.ifmo.collectionmanagingservice.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.ifmo.collectionmanagingservice.model.City;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@XmlRootElement(name = "GetCityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCityResponse extends BaseResponse {

    @XmlElement
    private City city;
}
