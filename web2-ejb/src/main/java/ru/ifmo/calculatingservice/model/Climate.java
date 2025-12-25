package ru.ifmo.calculatingservice.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "climate")
@XmlEnum
public enum Climate {
    HUMIDCONTINENTAL,
    TUNDRA,
    DESERT
}
