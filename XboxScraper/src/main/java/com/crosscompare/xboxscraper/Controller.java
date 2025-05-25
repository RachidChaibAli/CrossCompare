package com.crosscompare.xboxscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private XboxScraper xboxScraper;

    @GetMapping("/hola")
    public String getEndpoint() {
        return "Resultados desde el microservicio Xbox Scraper";
    }

    @PostMapping("/juegos")
    public void juegos(@RequestParam String busqueda) throws Exception {
        xboxScraper.scrape(busqueda);
    }

}
