package ru.ifmo.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Enumeration;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class ApiProxyController {

    @Value("${backend.service1.url}")
    private String service1Url;

    @Value("${backend.service2.url}")
    private String service2Url;

    private final RestTemplate restTemplate;
    private final Random random = new Random();

    public ApiProxyController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        
        try {
            // Получаем путь после /api
            String path = request.getRequestURI().substring("/api".length());
            
            // Выбираем бэкенд в зависимости от пути:
            // /api/route/* -> service2
            // /api/* -> service1
            String backendUrl;
            if (path.startsWith("/route")) {
                backendUrl = service2Url;
            } else {
                backendUrl = service1Url;
            }
            
            // Добавляем query параметры
            String queryString = request.getQueryString();
            String fullPath = path;
            if (queryString != null && !queryString.isEmpty()) {
                fullPath = path + "?" + queryString;
            }
            
            // Формируем полный URL
            String targetUrl = backendUrl + fullPath;
            
            // Копируем заголовки
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Пропускаем некоторые заголовки
                if (!headerName.equalsIgnoreCase("host") && 
                    !headerName.equalsIgnoreCase("content-length")) {
                    headers.add(headerName, request.getHeader(headerName));
                }
            }
            
            // Создаем запрос
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            
            // Выполняем запрос
            ResponseEntity<String> response = restTemplate.exchange(
                URI.create(targetUrl),
                HttpMethod.valueOf(request.getMethod()),
                entity,
                String.class
            );
            
            return response;
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
