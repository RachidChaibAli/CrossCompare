package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SteamApiService {
    private final RestClient steamRestClient;

    @Autowired
    public SteamApiService(RestClient steamRestClient) {
        this.steamRestClient = steamRestClient;
    }

    public String buscarJuegos() {
        return steamRestClient.get()
                .uri("/hola") // Ruta relativa en el microservicio steam-api
                .retrieve()
                .body(String.class);
    }
}
