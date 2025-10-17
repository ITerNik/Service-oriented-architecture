package ru.ifmo.collectionmanagingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Embeddable
public class Human {
    @NotNull
    @Min(1)
    @Column(name = "governor_height", nullable = false)
    private Double height;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "governor_birthday")
    private LocalDate birthday;

    public Human() {}

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
}
