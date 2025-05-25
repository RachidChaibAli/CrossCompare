package com.crosscompare.unificador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class UnificadorStreamListener {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private Unificador unificador;

    @Async
    public void escucharUnificadorStream() throws Exception {
        while (true) {
            List<MapRecord<String, Object, Object>> mensajes = redisTemplate.opsForStream().read(
                    Consumer.from("UnificadorGrupo", "unificador-consumer"),
                    StreamReadOptions.empty().count(10).block(Duration.ofMillis(1000)),
                    StreamOffset.create("UnificadorStream", ReadOffset.lastConsumed())
            );
            if (mensajes != null) {
                for (MapRecord<String, Object, Object> mensaje : mensajes) {
                    // Unifica el mensaje recibido (no necesita parámetro busqueda)
                    try {
                        unificador.unificarMensaje(mensaje);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    redisTemplate.opsForStream().acknowledge("UnificadorStream", "UnificadorGrupo", mensaje.getId());
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
