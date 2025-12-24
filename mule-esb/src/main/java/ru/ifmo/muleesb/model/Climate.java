package ru.ifmo.muleesb.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "Climate")
@XmlEnum
public enum Climate {
    HUMIDCONTINENTAL,
    TUNDRA,
    DESERT
}
