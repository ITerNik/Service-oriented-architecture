package ru.ifmo.front.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.ifmo.front")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
        @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Обработка статических ресурсов
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0)
                .resourceChain(false);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Для корня перенаправляем на index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}