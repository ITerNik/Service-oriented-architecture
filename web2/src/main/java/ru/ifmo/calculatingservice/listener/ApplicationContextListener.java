package ru.ifmo.calculatingservice.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.ifmo.calculatingservice.service.RouteService;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            String serviceUrl = System.getenv("COLLECTION_MANAGING_SERVICE_URL");
            if (serviceUrl == null) {
                serviceUrl = "http://mule-esb:8081/ws/cities?wsdl";
            }

            RouteService routeService = new RouteService();
            routeService.setServiceUrl(serviceUrl);
            routeService.init();

            sce.getServletContext().setAttribute("routeService", routeService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RouteService", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
