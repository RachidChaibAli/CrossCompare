package com.crosscompare.unificador;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    @LoadBalanced
    public RestClient playstationRestClient() {
        return RestClient.builder()
                .baseUrl("http://playstation-scraper") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }

    @Bean
    @LoadBalanced
    public RestClient xboxRestClient() {
        return RestClient.builder()
                .baseUrl("http://xbox-scraper") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }

    @Bean
    @LoadBalanced
    public RestClient steamRestClient() {
        return RestClient.builder()
                .baseUrl("http://steam-api") // Nombre del servicio y puerto interno en Docker Compose
                .build();
    }
}
