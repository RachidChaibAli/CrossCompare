package com.crosscompare.unificador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private Unificador unificador;

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private final PlaystationScraperService playstationScraperService;
    @Autowired
    private final XboxScraperService xboxScraperService;
    @Autowired
    private final SteamApiService steamApiService;

    @Autowired
    private final BuscadorPrecio buscadorPrecio;

    public Controller(PlaystationScraperService playstationScraperService,
                      XboxScraperService xboxScraperService,
                      SteamApiService steamApiService, BuscadorPrecio buscadorPrecio) {
        this.playstationScraperService = playstationScraperService;
        this.xboxScraperService = xboxScraperService;
        this.steamApiService = steamApiService;
        this.buscadorPrecio = buscadorPrecio;
    }

    @GetMapping("/juegos")
    public ArrayList<PreciosJuego> getJuegos(@RequestParam String busqueda) throws JsonProcessingException {
        if(busqueda == null || busqueda.isEmpty()) {
            return null;
        }
        playstationScraperService.buscarJuegosAsync(busqueda);
        xboxScraperService.buscarJuegosAsync(busqueda);
        steamApiService.buscarJuegosAsync(busqueda);

        unificador.unificarDatos(busqueda);

        ArrayList<PreciosJuego> juegos = buscadorPrecio.obtenerPreciosPorBusqueda(busqueda);

        return juegos;
    }

    @GetMapping("/error")
    public String getError() {
        return "Error en la API Gateway";
    }

}
