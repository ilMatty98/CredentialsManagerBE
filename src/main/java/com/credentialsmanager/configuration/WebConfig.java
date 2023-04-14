package com.credentialsmanager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${fe.endpoint}")
    private String endpointFe;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/authentication/**")
                .allowedOrigins(endpointFe)
                .allowedMethods("GET", "POST")
                .exposedHeaders("Content-Type") // Without headers
                .allowCredentials(false); // Without cookie
    }
}
