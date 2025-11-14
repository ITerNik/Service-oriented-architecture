package ru.ifmo.collectionmanagingservice.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlRootElement(name = "DeleteCityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteCityResponse extends BaseResponse {}
