package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SteamApiService {
    private final RestClient steamRestClient;

    @Autowired
    public SteamApiService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.steamRestClient = restClientBuilder
                .baseUrl("lb://steam-api") // Nombre del servicio en Eureka
                .build();
    }

    @Async
    public void buscarJuegosAsync(String busqueda) {
        try {
            steamRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/juegos")
                            .queryParam("busqueda", busqueda)
                            .build())
                    .retrieve();
        } catch (Exception e) {
            System.err.println("Error disparando SteamAPI: " + e.getMessage());
        }
    }
}