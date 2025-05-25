package com.crosscompare.unificador;

import com.crosscompare.unificador.jpa.Videojuego;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Unificador {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private com.crosscompare.unificador.jpa.VideojuegoService videojuegoService;
    @Autowired
    private com.crosscompare.unificador.jpa.PlataformaService plataformaService;
    @Autowired
    private com.crosscompare.unificador.jpa.PrecioService precioService;
    @Autowired
    private com.crosscompare.unificador.jpa.BusquedaService busquedaService;
    @Autowired
    private com.crosscompare.unificador.jpa.PerteneceService perteneceService;

    /**
     * Intenta parsear una fecha en varios formatos comunes y la devuelve como LocalDate.
     * Si no puede parsear, devuelve null.
     */
    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        String[] formatos = {
            "yyyy-MM-dd", // ISO
            "dd/MM/yyyy",
            "d/M/yyyy",
            "d-MM-yyyy",
            "dd-MM-yyyy",
            "yyyy/MM/dd"
        };
        for (String formato : formatos) {
            try {
                return LocalDate.parse(fecha, DateTimeFormatter.ofPattern(formato));
            } catch (DateTimeParseException ignored) {}
        }
        // Último intento: parseo directo ISO
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException ignored) {}
        return null;
    }

    /**
     * Unifica un objeto Juego de cualquier plataforma a JuegoFormateado,
     * parseando precio y fechaLanzamiento correctamente.
     */
    private JuegoFormateado unificarJuego(Juego juego) {
        // Parseo robusto de precio
        BigDecimal precio = null;
        if (juego.precio() != null && !juego.precio().isBlank()) {
            String precioStr = juego.precio()
                .replaceAll("[a-zA-Z€$+ ]", "") // quita letras, símbolos y espacios
                .replace(",", ".");
            if (juego.precio().toLowerCase().contains("gratis")) {
                precio = BigDecimal.ZERO;
            } else if (!precioStr.isBlank()) {
                try {
                    precio = new BigDecimal(precioStr);
                } catch (NumberFormatException _) {
                }
            }
        }
        // Parseo robusto de fecha
        LocalDate fechaLanzamiento = parseFecha(juego.fechaLanzamiento());
        return new JuegoFormateado(
            juego.nombre(),
            juego.descripcion(),
            fechaLanzamiento,
            juego.desarrollador(),
            juego.plataforma(),
            precio,
            juego.valoracion(),
            juego.boxartUrl()
        );
    }

    public ArrayList<Map<String, JuegoFormateado>> unificarDatos(String busqueda) throws JsonProcessingException {

        ArrayList<Map<String, JuegoFormateado>> juegos = new ArrayList<>();

        List<MapRecord<String, Object, Object>> mensajes = redisTemplate.opsForStream().read(
                Consumer.from("UnificadorGrupo", "Consumidor1"),
                StreamReadOptions.empty().count(10),
                StreamOffset.create("UnificadorStream", ReadOffset.lastConsumed())
        );

        if (mensajes != null && !mensajes.isEmpty()) {
            for (MapRecord<String, Object, Object> mensaje : mensajes) {
                Map<Object, Object> valores = mensaje.getValue();

                String nombreProductor = (String) valores.get("productor");

                Object juegoObject = valores.get("juego");

                // Instancia de ObjectMapper (puedes inyectarla con @Autowired si lo prefieres)
                ObjectMapper mapper = new ObjectMapper();

                // Si el objeto es String (JSON), deserializa con readValue
                Juego juego;
                if (juegoObject instanceof String json) {
                    juego = mapper.readValue(json, Juego.class);
                } else {
                    juego = mapper.convertValue(juegoObject, Juego.class);
                }

                // Unificación genérica
                JuegoFormateado formateado = unificarJuego(juego);

                if( formateado.nombre() == null || formateado.nombre().isBlank() || formateado.plataforma() == null || formateado.plataforma().isBlank()) {
                    continue; // Si el juego no tiene nombre o plataforma, lo ignoramos
                }
                if( formateado.precio() == null || formateado.precio().compareTo(BigDecimal.ZERO) < 0) {
                    continue;
                }

                List<Videojuego> juegosCoincidentes = buscarJuegosCoincidentes(formateado);

                if (juegosCoincidentes != null && !juegosCoincidentes.isEmpty()) {
                    // Si el juego ya existe, agrega el precio
                    agregarPrecioSiJuegoExiste(formateado, juegosCoincidentes);
                } else {
                    // Si no existe, crea el juego completo
                    crearJuegoCompleto(formateado, busqueda);
                }
                // Aquí puedes guardar el formateado en la base de datos o continuar el flujo
                // Por ejemplo: juegoFormateadoService.save(formateado);
                Map<String, JuegoFormateado> formateadoMapa = new HashMap<>();
                formateadoMapa.put(nombreProductor, formateado);
                juegos.add(formateadoMapa);
            }
        }

        return juegos;
    }

    /**
     * Crea un videojuego, plataforma y precio en orden si no existe el juego.
     */
    public void crearJuegoCompleto(JuegoFormateado formateado, String terminoBusqueda) {
        // Crear Videojuego
        com.crosscompare.unificador.jpa.Videojuego vj = new com.crosscompare.unificador.jpa.Videojuego();
        vj.setNombre(formateado.nombre());
        vj.setFechalanzamiento(formateado.fechaLanzamiento());
        vj.setDesarrolladora(formateado.desarrollador());
        vj.setDescripcion(formateado.descripcion());
        vj.setBoxartUrl(formateado.boxartUrl());
        vj = videojuegoService.save(vj);
        // Soporte para múltiples plataformas
        String[] plataformas = formateado.plataforma().split(",");
        for (String plataformaNombre : plataformas) {
            String nombrePlataforma = plataformaNombre.trim();
            if (nombrePlataforma.isBlank()) continue;
            com.crosscompare.unificador.jpa.Plataforma plataforma = plataformaService.findOrCreateByNombre(nombrePlataforma, null);
            // Crear relación Pertenece
            com.crosscompare.unificador.jpa.PerteneceId perteneceId = new com.crosscompare.unificador.jpa.PerteneceId();
            perteneceId.setIdvideojuego(vj.getId());
            perteneceId.setIdplataforma(plataforma.getId());
            if (perteneceService.findById(perteneceId).isEmpty()) {
                com.crosscompare.unificador.jpa.Pertenece pertenece = new com.crosscompare.unificador.jpa.Pertenece();
                pertenece.setId(perteneceId);
                pertenece.setIdvideojuego(vj);
                pertenece.setIdplataforma(plataforma);
                perteneceService.save(pertenece);
            }
            // Crear Precio
            crearPrecio(vj, plataforma, formateado.precio());
        }
        // Crear Busqueda (solo una vez por juego)
        com.crosscompare.unificador.jpa.BusquedaId busquedaId = new com.crosscompare.unificador.jpa.BusquedaId();
        busquedaId.setIdvideojuego(vj.getId());
        busquedaId.setTermino(terminoBusqueda);
        com.crosscompare.unificador.jpa.Busqueda busqueda = new com.crosscompare.unificador.jpa.Busqueda();
        busqueda.setId(busquedaId);
        busqueda.setFecha(Instant.now());
        busqueda.setIdvideojuego(vj); // Relación ManyToOne correctamente asignada
        busquedaService.save(busqueda);
    }

    /**
     * Devuelve una lista de videojuegos que coincidan por nombre, fecha y desarrollador (puede ser vacía).
     */
    public List<com.crosscompare.unificador.jpa.Videojuego> buscarJuegosCoincidentes(JuegoFormateado formateado) {
        return videojuegoService.buscarJuegosCoincidentes(formateado);
    }

    /**
     * Si existe el juego, revisa si existe la plataforma, si no la crea, y añade el precio.
     * Ahora recibe la lista de juegos coincidentes (no hace findAll).
     */
    public void agregarPrecioSiJuegoExiste(JuegoFormateado formateado, List<com.crosscompare.unificador.jpa.Videojuego> juegosCoincidentes) {
        if (juegosCoincidentes == null || juegosCoincidentes.isEmpty()) return;
        com.crosscompare.unificador.jpa.Videojuego vj = juegosCoincidentes.getFirst(); // Si hay varios, toma el primero
        // Rellenar campos vacíos si es posible
        boolean modificado = false;
        if ((vj.getDescripcion() == null || vj.getDescripcion().isBlank()) && formateado.descripcion() != null && !formateado.descripcion().isBlank()) {
            vj.setDescripcion(formateado.descripcion());
            modificado = true;
        }
        if ((vj.getBoxartUrl() == null || vj.getBoxartUrl().isBlank()) && formateado.boxartUrl() != null && !formateado.boxartUrl().isBlank()) {
            vj.setBoxartUrl(formateado.boxartUrl());
            modificado = true;
        }
        if ((vj.getDesarrolladora() == null || vj.getDesarrolladora().isBlank()) && formateado.desarrollador() != null && !formateado.desarrollador().isBlank()) {
            vj.setDesarrolladora(formateado.desarrollador());
            modificado = true;
        }
        if (vj.getFechalanzamiento() == null && formateado.fechaLanzamiento() != null) {
            vj.setFechalanzamiento(formateado.fechaLanzamiento());
            modificado = true;
        }
        if (modificado) {
            videojuegoService.save(vj);
        }
        // Soporte para múltiples plataformas
        String[] plataformas = formateado.plataforma().split(",");
        for (String plataformaNombre : plataformas) {
            String nombrePlataforma = plataformaNombre.trim();
            if (nombrePlataforma.isBlank()) continue;
            com.crosscompare.unificador.jpa.Plataforma plataforma = plataformaService.findOrCreateByNombre(nombrePlataforma, null);
            // Crear relación Pertenece
            com.crosscompare.unificador.jpa.PerteneceId perteneceId = new com.crosscompare.unificador.jpa.PerteneceId();
            perteneceId.setIdvideojuego(vj.getId());
            perteneceId.setIdplataforma(plataforma.getId());
            if (perteneceService.findById(perteneceId).isEmpty()) {
                com.crosscompare.unificador.jpa.Pertenece pertenece = new com.crosscompare.unificador.jpa.Pertenece();
                pertenece.setId(perteneceId);
                pertenece.setIdvideojuego(vj);
                pertenece.setIdplataforma(plataforma);
                perteneceService.save(pertenece);
            }
            // Crear Precio
            crearPrecio(vj, plataforma, formateado.precio());
        }
    }

    private void crearPrecio(com.crosscompare.unificador.jpa.Videojuego vj, com.crosscompare.unificador.jpa.Plataforma plataforma, BigDecimal precioValor) {
        com.crosscompare.unificador.jpa.Precio precio = new com.crosscompare.unificador.jpa.Precio();
        com.crosscompare.unificador.jpa.PrecioId precioId = new com.crosscompare.unificador.jpa.PrecioId();
        precioId.setIdvideojuego(vj.getId());
        precioId.setIdplataforma(plataforma.getId());
        precioId.setFecha(LocalDate.now());
        precio.setId(precioId);
        precio.setIdvideojuego(vj);
        precio.setIdplataforma(plataforma);
        precio.setPrecio(precioValor);
        precioService.save(precio);
    }
}
