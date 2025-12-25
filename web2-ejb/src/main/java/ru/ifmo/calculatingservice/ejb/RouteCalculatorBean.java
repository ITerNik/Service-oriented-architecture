package ru.ifmo.calculatingservice.ejb;

import jakarta.ejb.Stateless;
import jakarta.xml.ws.Service;
import java.net.URL;
import java.util.Comparator;
import javax.xml.namespace.QName;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.PageResponse;

@Stateless
public class RouteCalculatorBean implements RouteCalculatorRemote {

    private CityManagementService getSoapClient(String serviceUrl) {
        try {
            URL wsdlUrl = new URL(serviceUrl);
            QName serviceName = new QName("http://ifmo.ru/muleesb/cities", "CityManagementService");
            Service service = Service.create(wsdlUrl, serviceName);
            return service.getPort(CityManagementService.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SOAP client", e);
        }
    }

    @Override
    public double calculateToMaxPopulated(String serviceUrl) {
        try {
            CityManagementService client = getSoapClient(serviceUrl);
            PageResponse cities = client.getCities(0, 1000, null, null);

            if (cities == null
                    || cities.getContent() == null
                    || cities.getContent().isEmpty()) {
                return 0.0;
            }

            City maxPopulated = cities.getContent().stream()
                    .max(Comparator.comparing(City::getPopulation))
                    .orElseThrow();

            return calculateDistance(
                    0,
                    0,
                    maxPopulated.getCoordinates().getX(),
                    maxPopulated.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance to max populated city", e);
        }
    }

    @Override
    public double calculateBetweenOldestAndNewest(String serviceUrl) {
        try {
            CityManagementService client = getSoapClient(serviceUrl);
            PageResponse cities = client.getCities(0, 1000, null, null);

            if (cities == null
                    || cities.getContent() == null
                    || cities.getContent().size() < 2) {
                return 0.0;
            }

            City oldest = cities.getContent().stream()
                    .min(Comparator.comparing(City::getCreationDate))
                    .orElseThrow();

            City newest = cities.getContent().stream()
                    .max(Comparator.comparing(City::getCreationDate))
                    .orElseThrow();

            return calculateDistance(
                    oldest.getCoordinates().getX(),
                    oldest.getCoordinates().getY(),
                    newest.getCoordinates().getX(),
                    newest.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance between oldest and newest cities", e);
        }
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
