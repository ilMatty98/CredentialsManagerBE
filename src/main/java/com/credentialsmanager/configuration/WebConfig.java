package com.credentialsmanager.configuration;

import com.credentialsmanager.interceptor.TokenInterceptor;
import com.credentialsmanager.service.TokenJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + DELETE_ACCOUNT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + SEND_HINT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.POST.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_AUTHENTICATION + DELETE_ACCOUNT)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.DELETE.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_USER + CHANGE_EMAIL)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.PATCH.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie

        registry.addMapping(BASE_PATH_USER + CHANGE_INFORMATION)
                .allowedOriginPatterns(endpointFe)
                .allowedMethods(HttpMethod.PUT.name())
                .allowedHeaders(AUTHORIZATION)
                .allowCredentials(false); // Without cookie
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var patterns = Stream.concat(
                Stream.of(CHANGE_PASSWORD, DELETE_ACCOUNT).map(element -> BASE_PATH_AUTHENTICATION + element),
                Stream.of(CHANGE_EMAIL, CHANGE_INFORMATION).map(element -> BASE_PATH_USER + element)
        ).toList();
        registry.addInterceptor(tokenInterceptor()).addPathPatterns(patterns);
    }

    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor(tokenJwtService);
    }
}
