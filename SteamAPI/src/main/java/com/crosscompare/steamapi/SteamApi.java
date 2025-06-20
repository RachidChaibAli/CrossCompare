package com.crosscompare.steamapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SteamApi {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestClient restClient;

    @Value("${api.key}")
    private String apiKey;

    public void consumirApi(String nombreJuego) {
        Map<String, Juego> idJuegos = buscarIdsPorNombre(nombreJuego);

        idJuegos = buscarJuegoPorId(idJuegos);

        idJuegos = buscarPrecioPorId(idJuegos);

        ArrayList<String> juegos = new ArrayList<>();
        idJuegos.forEach((id, juego) -> juegos.add(id + " - " + juego.toString()));

        for (Juego juego : idJuegos.values()) {
            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("productor", "SteamApi");
            try {
                ObjectMapper mapper = new ObjectMapper();
                mensaje.put("juego", mapper.writeValueAsString(juego));
            } catch (Exception e) {
                mensaje.put("juego", "{}");
            }
            redisTemplate.opsForStream().add("UnificadorStream", mensaje);
        }

    }

    public Map<String, Juego> buscarIdsPorNombre(String nombreJuego) {
        String endpoint = "search/v1";

        List<GameSearchResult> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(endpoint)
                        .queryParam("title", nombreJuego)
                        .queryParam("key", apiKey)
                        .queryParam("results", 6)
                        .build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {
                });

        if (response != null) {
            return response.stream()
                    .collect(Collectors.toMap(
                            GameSearchResult::id,
                            r -> new Juego(
                                    r.title(),
                                    "", // descripcion
                                    "", // fechaLanzamiento
                                    "", // desarrollador
                                    "", // plataforma
                                    "", // precio
                                    "", // valoracion
                                    r.assets() != null ? r.assets().boxart() : null, // boxartUrl
                                    nombreJuego // busqueda original
                            )
                    ));
        } else {
            return Map.of();
        }
    }

    public Map<String, Juego> buscarJuegoPorId(Map<String, Juego> juegosMap) {
        String endpoint = "info/v2";
        for (String id : juegosMap.keySet()) {
            GameInfoResult detalle = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(endpoint)
                    .queryParam("key", apiKey)
                    .queryParam("id", id)
                    .build())
                .retrieve()
                .body(GameInfoResult.class);
            if (detalle != null) {
                String desarrollador = detalle.publishers() != null && !detalle.publishers().isEmpty() ? detalle.publishers().getFirst().name() : "";
                Juego juegoViejo = juegosMap.get(id);
                Juego juegoNuevo = new Juego(
                    detalle.title() != null ? detalle.title() : juegoViejo.nombre(),
                    detalle.description() != null ? detalle.description() : juegoViejo.descripcion(),
                    detalle.releaseDate() != null ? detalle.releaseDate() : juegoViejo.fechaLanzamiento(),
                    !desarrollador.isEmpty() ? desarrollador : juegoViejo.desarrollador(),
                    "PC", // plataforma fija o puedes adaptarla si hay campo
                    juegoViejo.precio(), // se mantiene el precio original (vacío)
                    "",
                    juegoViejo.boxartUrl(),
                    juegoViejo.busqueda()
                );
                juegosMap.put(id, juegoNuevo);
            }
            // Si no hay detalle, se deja el juego tal cual en el map
        }
        return juegosMap;
    }

    public Map<String, Juego> buscarPrecioPorId(Map<String, Juego> juegosMap) {
        String endpoint = "prices/v3";
        for (String id : juegosMap.keySet()) {
            List<String> bodyIds = List.of(id);
            GamePriceResult[] resultados = restClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(endpoint)
                    .queryParam("country", "ES")
                    .queryParam("key", apiKey)
                    .queryParam("shops", 61)
                    .build())
                .body(bodyIds)
                .retrieve()
                .body(GamePriceResult[].class);
            if (resultados != null && resultados.length > 0) {
                GamePriceResult resultado = resultados[0];
                if (resultado != null && resultado.deals() != null && resultado.deals().length > 0) {
                    GamePriceResult.Deal deal = resultado.deals()[0];
                    if (deal != null && deal.price() != null) {
                        Juego juegoViejo = juegosMap.get(id);
                        if (juegoViejo != null) {
                            String precio = deal.price().amount() + " " + deal.price().currency();
                            Juego juegoNuevo = new Juego(
                                juegoViejo.nombre(),
                                juegoViejo.descripcion(),
                                juegoViejo.fechaLanzamiento(),
                                juegoViejo.desarrollador(),
                                juegoViejo.plataforma(),
                                precio,
                                juegoViejo.valoracion(),
                                juegoViejo.boxartUrl(),
                                juegoViejo.busqueda()
                            );
                            juegosMap.put(id, juegoNuevo);
                        }
                    }
                }
            }
        }
        return juegosMap;
    }

}
