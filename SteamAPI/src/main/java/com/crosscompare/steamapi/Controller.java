package com.crosscompare.steamapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private SteamApi steamApi;

    @GetMapping("/hola")
    public String getEndpoint() {
        return "Resultados desde el microservicio Steam Scraper";
    }

    @PostMapping("/juegos")
    public void getJuegos(@RequestParam String busqueda) {
        if(busqueda != null && !busqueda.isEmpty()) {
            steamApi.consumirApi(busqueda);
        }

    }
}
