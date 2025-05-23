package com.crosscompare.playstationscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class Controller {

    // Inyectar el servicio de PlaystationScraper

    @Autowired
    private PlaystationScraper playstationScraper;

    @GetMapping("/hola")
    public String hola() {
        return "Resultados desde el microservicio playstation-scraper";
    }

    @GetMapping("/juegos")
    public String juegos() throws IOException, ExecutionException, InterruptedException {
        return playstationScraper.scrape("call");
    }
}
