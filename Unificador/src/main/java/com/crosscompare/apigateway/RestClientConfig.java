package com.crosscompare.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient playstationRestClient() {
        return RestClient.builder()
                .baseUrl("http://playstation-scraper:8080") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }

    @Bean
    public RestClient xboxRestClient() {
        return RestClient.builder()
                .baseUrl("http://xbox-scraper:8080") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }

    @Bean
    public RestClient steamRestClient() {
        return RestClient.builder()
                .baseUrl("http://steam-api:8080") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }
}
