package ru.ifmo.muleesb.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@Component
public class Web1IntegrationRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Web1IntegrationRoute.class);

    @Value("${web1.service.url}")
    private String web1ServiceUrl;

    @Override
    public void configure() throws Exception {

        JacksonDataFormat cityFormat = new JacksonDataFormat(City.class);
        JacksonDataFormat pageResponseFormat = new JacksonDataFormat(PageResponse.class);

        onException(Exception.class)
                .log("Error in route: ${exception.message}")
                .log("Stack trace: ${exception.stacktrace}")
                .handled(true)
                .setBody(constant(null));

        from("direct:getCities")
                .routeId("getCitiesRoute")
                .log("[Camel] getCities called. Incoming params: ${header.params}")
                .process(exchange -> {
                    Object[] params = exchange.getIn().getBody(Object[].class);
                    if (params == null || params.length < 2) {
                        throw new IllegalArgumentException("Invalid params");
                    }
                    Integer page = (Integer) params[0];
                    Integer size = (Integer) params[1];
                    String url = web1ServiceUrl + "/cities?page=" + page + "&size=" + size;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_PATH, "/cities");
                    exchange.getIn().setHeader(Exchange.HTTP_QUERY, "page=" + page + "&size=" + size);
                    exchange.getIn().setHeader("X-Log-Params", "page=" + page + ", size=" + size);
                    logger.info("[Camel] Preparing HTTP GET to web1: {}", url);
                })
                .log("[Camel] HTTP GET to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(pageResponseFormat)
                .log("[Camel] Unmarshalled PageResponse: ${body}")
                .process(exchange -> {
                    Object body = exchange.getIn().getBody();
                    exchange.getMessage().setBody(body);
                });

        from("direct:getCityById")
                .routeId("getCityByIdRoute")
                .log("[Camel] getCityById called. Incoming id: ${body}")
                .process(exchange -> {
                    Long id = exchange.getIn().getBody(Long.class);
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    logger.info("[Camel] Preparing HTTP GET to web1: {}", url);
                })
                .log("[Camel] HTTP GET to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(cityFormat)
                .log("[Camel] Unmarshalled City: ${body}");

        from("direct:createCity")
                .routeId("createCityRoute")
                .log("[Camel] createCity called. Incoming city: ${body}")
                .marshal(cityFormat)
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    logger.info("[Camel] Preparing HTTP POST to web1: {}", url);
                })
                .log("[Camel] HTTP POST to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(cityFormat)
                .log("[Camel] Unmarshalled City: ${body}");

        from("direct:updateCity")
                .routeId("updateCityRoute")
                .log("[Camel] updateCity called. Incoming params: ${body}")
                .process(exchange -> {
                    Object[] params = exchange.getIn().getBody(Object[].class);
                    Long id = (Long) params[0];
                    City city = (City) params[1];
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    exchange.getIn().setBody(city);
                    logger.info("[Camel] Preparing HTTP PUT to web1: {}", url);
                })
                .marshal(cityFormat)
                .log("[Camel] HTTP PUT to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(cityFormat)
                .log("[Camel] Unmarshalled City: ${body}");

        from("direct:deleteCity")
                .routeId("deleteCityRoute")
                .log("[Camel] deleteCity called. Incoming id: ${body}")
                .process(exchange -> {
                    Long id = exchange.getIn().getBody(Long.class);
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "DELETE");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    logger.info("[Camel] Preparing HTTP DELETE to web1: {}", url);
                })
                .log("[Camel] HTTP DELETE to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}");

        from("direct:deleteCityByMeters")
                .routeId("deleteCityByMetersRoute")
                .log("[Camel] deleteCityByMeters called. Incoming meters: ${body}")
                .process(exchange -> {
                    Integer meters = exchange.getIn().getBody(Integer.class);
                    String url = web1ServiceUrl + "/cities/metersAboveSeaLevel/" + meters;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "DELETE");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    logger.info("[Camel] Preparing HTTP DELETE to web1: {}", url);
                })
                .log("[Camel] HTTP DELETE to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}");

        from("direct:getCityWithMinimalName")
                .routeId("getCityWithMinimalNameRoute")
                .log("[Camel] getCityWithMinimalName called.")
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities/minimal-name";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    logger.info("[Camel] Preparing HTTP GET to web1: {}", url);
                })
                .log("[Camel] HTTP GET to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(cityFormat)
                .log("[Camel] Unmarshalled City: ${body}");

        from("direct:getCityWithMaximalClimate")
                .routeId("getCityWithMaximalClimateRoute")
                .log("[Camel] getCityWithMaximalClimate called.")
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities/maximal-climate";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    logger.info("[Camel] Preparing HTTP GET to web1: {}", url);
                })
                .log("[Camel] HTTP GET to web1: ${header.CamelHttpUri}")
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("[Camel] HTTP response from web1: ${body}")
                .unmarshal(cityFormat)
                .log("[Camel] Unmarshalled City: ${body}");
    }
}
