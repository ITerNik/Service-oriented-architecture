package ru.ifmo.calculatingservice.ejb;

import jakarta.ejb.Remote;

@Remote
public interface RouteCalculatorRemote {

    double calculateToMaxPopulated(String serviceUrl);

    double calculateBetweenOldestAndNewest(String serviceUrl);
}
