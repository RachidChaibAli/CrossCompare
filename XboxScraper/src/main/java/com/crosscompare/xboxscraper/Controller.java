package com.crosscompare.xboxscraper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/hola")
    public String getEndpoint() {
        return "Resultados desde el microservicio Xbox Scraper";
    }
}
