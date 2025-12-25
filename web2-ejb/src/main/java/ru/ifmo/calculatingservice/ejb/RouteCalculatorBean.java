package ru.ifmo.calculatingservice.ejb;

import jakarta.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.PageResponse;

@Stateless
public class RouteCalculatorBean implements RouteCalculatorRemote {

    private PageResponse callSoapService(String wsdlUrl, int page, int size) throws Exception {
        String endpoint = wsdlUrl.replace("?wsdl", "");

        String soapRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:ns=\"http://ifmo.ru/muleesb/cities\">"
                + "<soap:Body>"
                + "<ns:getCities>"
                + "<page>"
                + page
                + "</page>"
                + "<size>"
                + size
                + "</size>"
                + "</ns:getCities>"
                + "</soap:Body>"
                + "</soap:Envelope>";

        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapRequest.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        String xmlResponse = response.toString();
        System.out.println("SOAP Response: " + xmlResponse);
        return parseSoapResponse(xmlResponse);
    }

    private PageResponse parseSoapResponse(String xml) {
        PageResponse pageResponse = new PageResponse();
        java.util.List<City> cities = new java.util.ArrayList<>();

        Pattern[] cityPatterns = {
            Pattern.compile("<city>(.*?)</city>", Pattern.DOTALL),
            Pattern.compile("<ns\\d*:city>(.*?)</ns\\d*:city>", Pattern.DOTALL),
            Pattern.compile("<[^:]+:city[^>]*>(.*?)</[^:]+:city>", Pattern.DOTALL)
        };

        for (Pattern cityPattern : cityPatterns) {
            Matcher cityMatcher = cityPattern.matcher(xml);
            while (cityMatcher.find()) {
                String cityXml = cityMatcher.group(1);
                City city = parseCity(cityXml);
                if (city != null) {
                    cities.add(city);
                }
            }
            if (!cities.isEmpty()) {
                break;
            }
        }

        System.out.println("Parsed " + cities.size() + " cities");
        pageResponse.setContent(cities);
        return pageResponse;
    }

    private City parseCity(String xml) {
        City city = new City();
        try {
            city.setId(extractLong(xml, "id"));
            city.setName(extractString(xml, "name"));
            city.setPopulation(extractInt(xml, "population"));

            String creationDateStr = extractString(xml, "creationDate");
            if (!creationDateStr.isEmpty()) {
                city.setCreationDate(java.time.LocalDate.parse(creationDateStr.split("T")[0]));
            } else {
                city.setCreationDate(java.time.LocalDate.now());
            }

            ru.ifmo.calculatingservice.model.Coordinates coords = new ru.ifmo.calculatingservice.model.Coordinates();
            coords.setX(extractDouble(xml, "x"));
            coords.setY(extractDouble(xml, "y"));
            city.setCoordinates(coords);

            return city;
        } catch (Exception e) {
            System.err.println("Failed to parse city: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String extractString(String xml, String tag) {
        Pattern pattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");
        Matcher matcher = pattern.matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }

    private Long extractLong(String xml, String tag) {
        String value = extractString(xml, tag);
        return value.isEmpty() ? null : Long.parseLong(value);
    }

    private Integer extractInt(String xml, String tag) {
        String value = extractString(xml, tag);
        return value.isEmpty() ? null : Integer.parseInt(value);
    }

    private Double extractDouble(String xml, String tag) {
        String value = extractString(xml, tag);
        return value.isEmpty() ? null : Double.parseDouble(value);
    }

    @Override
    public double calculateToMaxPopulated(String serviceUrl) {
        try {
            PageResponse cities = callSoapService(serviceUrl, 0, 1000);

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
            PageResponse cities = callSoapService(serviceUrl, 0, 1000);

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
