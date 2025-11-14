package ru.ifmo.calculatingservice.client;

import jakarta.xml.ws.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ifmo.calculatingservice.model.City;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.List;

@Component
public class CityServiceSoapClient {

    @Value("${collection-managing-service.soap.url}")
    private String serviceUrl;

    private CityWebServiceClient getPort() {
        try {
            URL wsdlURL = new URL(serviceUrl + "?wsdl");
            QName SERVICE_NAME = new QName("http://soap.collectionmanagingservice.ifmo.ru/", "CityService");
            Service service = Service.create(wsdlURL, SERVICE_NAME);
            return service.getPort(CityWebServiceClient.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SOAP client", e);
        }
    }

    public List<City> getCities(int page, int size, String sort, String filters) {
        return getPort().getCities(page, size, sort, filters);
    }

    public City getCityById(String id) {
        return getPort().getCityById(id);
    }

    public City createCity(City city) {
        return getPort().createCity(city);
    }

    public City updateCity(String id, City city) {
        return getPort().updateCity(id, city);
    }

    public boolean deleteCity(String id) {
        return getPort().deleteCity(id);
    }

    public boolean deleteCityByMeters(Integer meters) {
        return getPort().deleteCityByMeters(meters);
    }

    public City getCityWithMinName() {
        return getPort().getCityWithMinName();
    }

    public City getCityWithMaxClimate() {
        return getPort().getCityWithMaxClimate();
    }
}
