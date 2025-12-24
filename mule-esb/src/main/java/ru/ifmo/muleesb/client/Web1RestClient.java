package ru.ifmo.muleesb.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@Component
public class Web1RestClient {

    private final RestTemplate restTemplate;
    private final String web1BaseUrl;
    private final ObjectMapper objectMapper;

    public Web1RestClient(@Value("${web1.service.url}") String web1BaseUrl) {
        this.web1BaseUrl = web1BaseUrl;
        this.restTemplate = createRestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private RestTemplate createRestTemplate() {
        try {
            // Create trust manager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            return new RestTemplate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate", e);
        }
    }

    public PageResponse getCities(Integer page, Integer size, String sort) {
        String url = web1BaseUrl + "/cities?page=" + page + "&size=" + size;
        if (sort != null && !sort.isEmpty()) {
            url += "&sort=" + sort;
        }

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);

        Map<String, Object> body = response.getBody();
        PageResponse pageResponse = new PageResponse();

        if (body != null && body.containsKey("content")) {
            pageResponse.setContent(objectMapper.convertValue(
                    body.get("content"),
                    objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, City.class)));
            pageResponse.setTotalElements(((Number) body.getOrDefault("totalElements", 0)).longValue());
            pageResponse.setTotalPages(((Number) body.getOrDefault("totalPages", 0)).intValue());
            pageResponse.setCurrentPage(((Number) body.getOrDefault("number", 0)).intValue());
            pageResponse.setPageSize(((Number) body.getOrDefault("size", 0)).intValue());
        }

        return pageResponse;
    }

    public City createCity(City city) {
        String url = web1BaseUrl + "/cities";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<City> request = new HttpEntity<>(city, headers);

        ResponseEntity<City> response = restTemplate.exchange(url, HttpMethod.POST, request, City.class);

        return response.getBody();
    }

    public City getCityById(Long id) {
        String url = web1BaseUrl + "/cities/" + id;
        ResponseEntity<City> response = restTemplate.exchange(url, HttpMethod.GET, null, City.class);

        return response.getBody();
    }

    public City updateCity(Long id, City city) {
        String url = web1BaseUrl + "/cities/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<City> request = new HttpEntity<>(city, headers);

        ResponseEntity<City> response = restTemplate.exchange(url, HttpMethod.PUT, request, City.class);

        return response.getBody();
    }

    public void deleteCity(Long id) {
        String url = web1BaseUrl + "/cities/" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
    }

    public void deleteCityByMeters(Integer meters) {
        String url = web1BaseUrl + "/cities/metersAboveSeaLevel/" + meters;
        restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
    }

    public City getCityWithMinimalName() {
        String url = web1BaseUrl + "/cities/minimal-name";
        ResponseEntity<City> response = restTemplate.exchange(url, HttpMethod.GET, null, City.class);

        return response.getBody();
    }

    public City getCityWithMaximalClimate() {
        String url = web1BaseUrl + "/cities/maximal-climate";
        ResponseEntity<City> response = restTemplate.exchange(url, HttpMethod.GET, null, City.class);

        return response.getBody();
    }
}
