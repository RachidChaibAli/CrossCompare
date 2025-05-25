package com.crosscompare.playstationscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class BusquedaStreamListener {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PlaystationScraper playstationScraper;

    //@Async
    public void escucharBusquedaStream() throws IOException {
        while (true) {
            List<MapRecord<String, Object, Object>> mensajes = redisTemplate.opsForStream().read(
                    Consumer.from("BusquedaGrupoPlaystation", "playstation-consumer"),
                    StreamReadOptions.empty().count(10).block(Duration.ofMillis(1000)),
                    StreamOffset.create("BusquedaStream", ReadOffset.lastConsumed())
            );
            if (mensajes != null) {
                for (MapRecord<String, Object, Object> mensaje : mensajes) {
                    Object busqueda = mensaje.getValue().get("busqueda");
                    if (busqueda != null) {
                        playstationScraper.scrape(busqueda.toString());
                    }
                    // Hacer ACK del mensaje procesado
                    redisTemplate.opsForStream().acknowledge("BusquedaStream", "BusquedaGrupoPlaystation", mensaje.getId());
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
