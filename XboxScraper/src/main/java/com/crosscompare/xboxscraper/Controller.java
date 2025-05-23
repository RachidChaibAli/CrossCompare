package com.crosscompare.xboxscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class Controller {

    @Autowired
    private XboxScraper xboxScraper;

    @GetMapping("/hola")
    public String getEndpoint() {
        return "Resultados desde el microservicio Xbox Scraper";
    }

    @GetMapping("/juegos")
    public ArrayList<Juego> juegos() throws Exception {
        return xboxScraper.scrape("call");
    }

}
