package ru.ifmo.collectionmanagingservice.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.ifmo.collectionmanagingservice.model.City;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@XmlRootElement(name = "GetCitiesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCitiesResponse extends BaseResponse {
    @XmlElementWrapper(name = "cities")
    @XmlElement(name = "city")
    private List<City> cities;
}
