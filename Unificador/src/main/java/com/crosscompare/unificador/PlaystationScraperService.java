package com.crosscompare.unificador;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PlaystationScraperService {

    private final RestClient restClient;

    @Autowired
    public PlaystationScraperService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://playstation-scraper") // Nombre del servicio en Eureka
                .build();
    }

    public String buscarJuegos() {
        return restClient.get()
                .uri("/hola") // Ruta relativa en el microservicio
                .retrieve()
                .body(String.class);
    }
}

