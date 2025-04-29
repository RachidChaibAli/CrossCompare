package com.crosscompare.playstationscraper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/hola")
    public String hola() {
        return "Resultados desde el microservicio playstation-scraper";
    }
}
