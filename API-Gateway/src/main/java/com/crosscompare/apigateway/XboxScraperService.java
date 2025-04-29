package com.crosscompare.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class XboxScraperService {
    private final RestClient xboxRestClient;

    @Autowired
    public XboxScraperService(RestClient xboxRestClient) {
        this.xboxRestClient = xboxRestClient;
    }

    public String buscarJuegos() {
        return xboxRestClient.get()
                .uri("/hola") // Ruta relativa en el microservicio xbox-scraper
                .retrieve()
                .body(String.class);
    }
}
