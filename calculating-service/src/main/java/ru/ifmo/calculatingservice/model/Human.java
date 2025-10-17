package ru.ifmo.calculatingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class Human {
    private Double height;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
}
