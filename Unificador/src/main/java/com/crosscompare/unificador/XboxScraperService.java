package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class XboxScraperService {
    private final RestClient xboxRestClient;

    @Autowired
    public XboxScraperService(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.xboxRestClient = restClientBuilder
                .baseUrl("http://xbox-scraper") // Nombre del servicio en Eureka
                .build();
    }

    public String buscarJuegos() {
        return xboxRestClient.get()
                .uri("/hola")
                .retrieve()
                .body(String.class);
    }
}
