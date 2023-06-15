package com.credentialsmanager.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.credentialsmanager.constants.UrlConstants.*;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${fe.endpoint}")
    private String endpointFe;
    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(BASE_PATH_AUTHENTICATION + SIGN_UP)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + LOG_IN)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + CHECK_EMAIL)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.GET.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + CONFIRM_EMAIL)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.PATCH.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + CHANGE_PASSWORD)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + DELETE_ACCOUNT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + SEND_HINT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + DELETE_ACCOUNT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.DELETE.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_USER + CHANGE_EMAIL)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.PATCH.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_USER + CHANGE_INFORMATION)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.PUT.name())
                .allowedHeaders("*")
                .allowCredentials(false); // Without cookie
    }
}
