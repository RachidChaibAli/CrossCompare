package com.crosscompare.unificador;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PlaystationScraperService {

    private final RestClient restClient;

    @Autowired
    public PlaystationScraperService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("lb://playstation-scraper") // Nombre del servicio en Eureka
                .build();
    }

    @Async
    public void buscarJuegosAsync(String busqueda) {
        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/juegos")
                            .queryParam("busqueda", busqueda)
                            .build())
                    .retrieve();
        } catch (Exception e) {
            System.err.println("Error disparando PlaystationScraper: " + e.getMessage());
        }
    }
}

