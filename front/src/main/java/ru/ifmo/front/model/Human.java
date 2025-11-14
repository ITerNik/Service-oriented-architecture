package ru.ifmo.front.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.ifmo.front.adapter.LocalDateAdapter;

import java.time.LocalDate;

@XmlAccessorType(XmlAccessType.FIELD)
public class Human {
    private Double height;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate birthday;

    public Human() {
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
