package com.crosscompare.unificador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Controller(PlaystationScraperService playstationScraperService,
                      XboxScraperService xboxScraperService,
                      SteamApiService steamApiService, BuscadorPrecio buscadorPrecio) {
        this.playstationScraperService = playstationScraperService;
        this.xboxScraperService = xboxScraperService;
        this.steamApiService = steamApiService;
        this.buscadorPrecio = buscadorPrecio;
    }

    @PostMapping("/juegos")
    public void postJuegos(@RequestParam String busqueda) {
        if(busqueda == null || busqueda.isEmpty()) {
            return;
        }
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("busqueda", busqueda);
        redisTemplate.opsForStream().add("BusquedaStream", mensaje);
    }

    @GetMapping("/juegos")
    public ArrayList<PreciosJuego> getJuegos(@RequestParam String busqueda) {
        if(busqueda == null || busqueda.isEmpty()) {
            return new ArrayList<>();
        }
        return buscadorPrecio.obtenerPreciosPorBusqueda(busqueda);
    }

    @GetMapping("/error")
    public String getError() {
        return "Error en la API Gateway";
    }

}
