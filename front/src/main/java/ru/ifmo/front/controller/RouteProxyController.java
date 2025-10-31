package ru.ifmo.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Enumeration;

@RestController
@RequestMapping("/route")
public class RouteProxyController {

    @Value("${backend.service2.url}")
    private String service2Url;

    private final RestTemplate restTemplate;

    public RouteProxyController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.PATCH })
    public ResponseEntity<String> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        try {
            String path = request.getRequestURI();

            String queryString = request.getQueryString();
            String fullPath = path;
            if (queryString != null && !queryString.isEmpty()) {
                fullPath = path + "?" + queryString;
            }

            String targetUrl = service2Url + fullPath;

            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!headerName.equalsIgnoreCase("host") &&
                        !headerName.equalsIgnoreCase("content-length")) {
                    headers.add(headerName, request.getHeader(headerName));
                }
            }

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    String.class);

            return response;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
