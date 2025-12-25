package ru.ifmo.calculatingservice.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "pageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PageResponse {
    @XmlElement
    private List<City> content;

    public List<City> getContent() {
        return content;
    }

    public void setContent(List<City> content) {
        this.content = content;
    }
}
