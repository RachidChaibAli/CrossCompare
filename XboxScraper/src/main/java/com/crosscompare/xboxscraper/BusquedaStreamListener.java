package com.crosscompare.xboxscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class BusquedaStreamListener {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private XboxScraper xboxScraper;

    //@Async
    public void escucharBusquedaStream() throws Exception {
        while (true) {
            List<MapRecord<String, Object, Object>> mensajes = redisTemplate.opsForStream().read(
                    Consumer.from("BusquedaGrupoXbox", "xbox-consumer"),
                    StreamReadOptions.empty().count(10).block(Duration.ofMillis(1000)),
                    StreamOffset.create("BusquedaStream", ReadOffset.lastConsumed())
            );
            if (mensajes != null) {
                for (MapRecord<String, Object, Object> mensaje : mensajes) {
                    Object busqueda = mensaje.getValue().get("busqueda");
                    if (busqueda != null) {
                        xboxScraper.scrape(busqueda.toString());
                    }
                    // Hacer ACK del mensaje procesado
                    redisTemplate.opsForStream().acknowledge("BusquedaStream", "BusquedaGrupoXbox", mensaje.getId());
                }
            }
            try {
                Thread.sleep(1000); // Evita bucle agresivo
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
