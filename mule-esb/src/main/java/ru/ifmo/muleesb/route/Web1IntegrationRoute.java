package ru.ifmo.muleesb.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@Component
public class Web1IntegrationRoute extends RouteBuilder {

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
                .log("Camel route: getCities with params ${header.params}")
                .process(exchange -> {
                    Object[] params = exchange.getIn().getHeader("params", Object[].class);
                    Integer page = (Integer) params[0];
                    Integer size = (Integer) params[1];
                    String sort = (String) params[2];

                    String url = web1ServiceUrl + "/cities?page=" + page + "&size=" + size + "&sort=" + sort;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(pageResponseFormat)
                .log("Response: ${body}");

        from("direct:getCityById")
                .routeId("getCityByIdRoute")
                .log("Camel route: getCityById ${body}")
                .process(exchange -> {
                    Long id = exchange.getIn().getBody(Long.class);
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(cityFormat);

        from("direct:createCity")
                .routeId("createCityRoute")
                .log("Camel route: createCity ${body}")
                .marshal(cityFormat)
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(cityFormat);

        from("direct:updateCity")
                .routeId("updateCityRoute")
                .log("Camel route: updateCity ${header.id} with ${body}")
                .marshal(cityFormat)
                .process(exchange -> {
                    Long id = exchange.getIn().getHeader("id", Long.class);
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(cityFormat);

        from("direct:deleteCity")
                .routeId("deleteCityRoute")
                .log("Camel route: deleteCity ${body}")
                .process(exchange -> {
                    Long id = exchange.getIn().getBody(Long.class);
                    String url = web1ServiceUrl + "/cities/" + id;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "DELETE");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false");

        from("direct:deleteCityByMeters")
                .routeId("deleteCityByMetersRoute")
                .log("Camel route: deleteCityByMeters ${body}")
                .process(exchange -> {
                    Integer meters = exchange.getIn().getBody(Integer.class);
                    String url = web1ServiceUrl + "/cities/metersAboveSeaLevel/" + meters;
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "DELETE");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false");

        from("direct:getCityWithMinimalName")
                .routeId("getCityWithMinimalNameRoute")
                .log("Camel route: getCityWithMinimalName")
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities/minimal-name";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(cityFormat);

        from("direct:getCityWithMaximalClimate")
                .routeId("getCityWithMaximalClimateRoute")
                .log("Camel route: getCityWithMaximalClimate")
                .process(exchange -> {
                    String url = web1ServiceUrl + "/cities/maximal-climate";
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader(Exchange.HTTP_URI, url);
                })
                .to("https://haproxy1:8443?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal(cityFormat);
    }
}
