package com.crosscompare.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {


    private final PlaystationScraperService playstationScraperService;
    private final XboxScraperService xboxScraperService;
    private final SteamApiService steamApiService;

    public Controller(PlaystationScraperService playstationScraperService,
                      XboxScraperService xboxScraperService,
                      SteamApiService steamApiService)  {
        this.playstationScraperService = playstationScraperService;
        this.xboxScraperService = xboxScraperService;
        this.steamApiService = steamApiService;
    }

    @GetMapping("/hola")
    public String getEndpoint() {
        StringBuilder resultados = new StringBuilder();
        resultados.append("Resultados de la API Gateway:\n");
        resultados.append(playstationScraperService.buscarJuegos());
        resultados.append("\n");
        resultados.append(xboxScraperService.buscarJuegos());
        resultados.append("\n");
        resultados.append(steamApiService.buscarJuegos());
        resultados.append("\n");
        return resultados.toString();
    }

    @GetMapping("/error")
    public String getError() {
        return "Error en la API Gateway";
    }

}
