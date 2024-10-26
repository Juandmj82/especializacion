package com.desafios.biblioteca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permitir acceso a todas las rutas de la API
                .allowedOrigins("http://localhost:3000") // Cambia esto por la URL de tu front-end
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
