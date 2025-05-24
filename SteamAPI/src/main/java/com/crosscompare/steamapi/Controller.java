package com.crosscompare.steamapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class Controller {

    @Autowired
    private SteamApi steamApi;

    @GetMapping("/hola")
    public String getEndpoint() {
        return "Resultados desde el microservicio Steam Scraper";
    }

    @GetMapping("/juegos")
    public ArrayList<String> getJuegos() {
        return steamApi.consumirApi("call of duty");
    }
}
