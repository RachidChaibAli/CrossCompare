package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class XboxScraperService {
    private final RestClient xboxRestClient;

    @Autowired
    public XboxScraperService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.xboxRestClient = restClientBuilder
                .baseUrl("lb://xbox-scraper") // Nombre del servicio en Eureka
                .build();
    }

    @Async
    public void buscarJuegosAsync(String busqueda) {
        try {
            xboxRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/juegos")
                            .queryParam("busqueda", busqueda)
                            .build())
                    .retrieve();
        } catch (Exception e) {
            System.err.println("Error disparando XboxScraper: " + e.getMessage());
        }
    }
}
