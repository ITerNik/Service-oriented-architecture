package ru.ifmo.collectionmanagingservice.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "climate")
public enum Climate {
    @XmlEnumValue("HUMIDCONTINENTAL")
    HUMIDCONTINENTAL,

    @XmlEnumValue("TUNDRA")
    TUNDRA,

    @XmlEnumValue("DESERT")
    DESERT
}
