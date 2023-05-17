package com.credentialsmanager.configuration;

import com.credentialsmanager.interceptor.TokenInterceptor;
import com.credentialsmanager.service.TokenJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

import static com.credentialsmanager.constants.UrlConstants.*;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${fe.endpoint}")
    private String endpointFe;
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";

    private final TokenJwtService tokenJwtService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(BASE_PATH + SIGN_UP)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + LOG_IN)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + CHECK_EMAIL)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.GET.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + CONFIRM_EMAIL)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.PATCH.name())
                .exposedHeaders(CONTENT_TYPE) // Without headers
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + CHANGE_PASSWORD)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + CHANGE_EMAIL)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.PATCH.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + CHANGE_INFORMATION)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.PUT.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + DELETE_ACCOUNT)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + SEND_HINT)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH + DELETE_ACCOUNT)
                .allowedOrigins(endpointFe)
                .allowedMethods(HttpMethod.DELETE.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var patterns = Stream.of(CHANGE_PASSWORD, CHANGE_EMAIL, CHANGE_INFORMATION, DELETE_ACCOUNT)
                .map(element -> BASE_PATH + element)
                .toList();
        registry.addInterceptor(new TokenInterceptor(tokenJwtService))
                .addPathPatterns(patterns);
    }
}
