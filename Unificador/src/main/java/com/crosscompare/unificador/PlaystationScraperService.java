package com.crosscompare.unificador;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PlaystationScraperService {
    private final RestClient playstationRestClient;

    @Autowired
    public PlaystationScraperService(RestClient playstationRestClient) {
        this.playstationRestClient = playstationRestClient;
    }

    public String buscarJuegos() {
        return playstationRestClient.get()
                .uri("/hola") // Ruta relativa en el microservicio playstation-scraper
                .retrieve()
                .body(String.class);
    }


}
