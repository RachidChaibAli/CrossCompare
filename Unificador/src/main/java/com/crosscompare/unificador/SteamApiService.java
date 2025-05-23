package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SteamApiService {
    private final RestClient steamRestClient;

    @Autowired
    public SteamApiService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.steamRestClient = restClientBuilder
                .baseUrl("http://steam-api") // Nombre del servicio en Eureka
                .build();
    }

    public String buscarJuegos() {
        return steamRestClient.get()
                .uri("/hola")
                .retrieve()
                .body(String.class);
    }
}