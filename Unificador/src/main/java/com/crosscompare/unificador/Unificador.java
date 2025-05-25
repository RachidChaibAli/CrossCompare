package com.crosscompare.unificador;

import com.crosscompare.unificador.jpa.*;
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
    private VideojuegoService videojuegoService;
    @Autowired
    private PlataformaService plataformaService;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private BusquedaService busquedaService;
    @Autowired
    private PerteneceService perteneceService;

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
        BigDecimal precio = parsePrecio(juego);
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
            juego.boxartUrl(),
            juego.busqueda()    
        );
    }

    private BigDecimal parsePrecio(Juego juego) {
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
        return precio;
    }

    // Nuevo método para unificar un solo mensaje
    public void unificarMensaje(MapRecord<String, Object, Object> mensaje) throws JsonProcessingException {
        Map<Object, Object> valores = mensaje.getValue();
        Object juegoObject = valores.get("juego");
        ObjectMapper mapper = new ObjectMapper();
        Juego juego;
        if (juegoObject instanceof String json) {
            juego = mapper.readValue(json, Juego.class);
        } else {
            juego = mapper.convertValue(juegoObject, Juego.class);
        }
        JuegoFormateado formateado = unificarJuego(juego);
        if( formateado.nombre() == null || formateado.nombre().isBlank() || formateado.plataforma() == null || formateado.plataforma().isBlank()) {
            return;
        }
        if( formateado.precio() == null || formateado.precio().compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        List<Videojuego> juegosCoincidentes = buscarJuegosCoincidentes(formateado);
        // Extraer el término de búsqueda del mensaje
        String terminoBusqueda = formateado.busqueda();
        if (juegosCoincidentes != null && !juegosCoincidentes.isEmpty()) {
            agregarPrecioSiJuegoExiste(formateado, juegosCoincidentes);
        } else {
            crearJuegoCompleto(formateado, terminoBusqueda); // Ahora sí se guarda el término
        }
    }

    /**
     * Crea un videojuego, plataforma y precio en orden si no existe el juego.
     */
    public void crearJuegoCompleto(JuegoFormateado formateado, String terminoBusqueda) {
        // Crear Videojuego
        Videojuego vj = new Videojuego();
        vj.setNombre(formateado.nombre());
        vj.setFechalanzamiento(formateado.fechaLanzamiento());
        vj.setDesarrolladora(formateado.desarrollador());
        vj.setDescripcion(formateado.descripcion());
        vj.setBoxartUrl(formateado.boxartUrl());
        vj = videojuegoService.save(vj);
        // Soporte para múltiples plataformas
        separarPlataformas(formateado, vj);
        // Crear Busqueda (solo una vez por juego)
        BusquedaId busquedaId = new BusquedaId();
        busquedaId.setIdvideojuego(vj.getId());
        busquedaId.setTermino(terminoBusqueda);
        Busqueda busqueda = new Busqueda();
        busqueda.setId(busquedaId);
        busqueda.setFecha(Instant.now());
        busqueda.setIdvideojuego(vj); // Relación ManyToOne correctamente asignada
        busquedaService.save(busqueda);
    }

    /**
     * Devuelve una lista de videojuegos que coincidan por nombre, fecha y desarrollador (puede ser vacía).
     */
    public List<Videojuego> buscarJuegosCoincidentes(JuegoFormateado formateado) {
        return videojuegoService.buscarJuegosCoincidentes(formateado);
    }

    /**
     * Si existe el juego, revisa si existe la plataforma, si no la crea, y añade el precio.
     * Ahora recibe la lista de juegos coincidentes (no hace findAll).
     */
    public void agregarPrecioSiJuegoExiste(JuegoFormateado formateado, List<Videojuego> juegosCoincidentes) {
        if (juegosCoincidentes == null || juegosCoincidentes.isEmpty()) return;
        Videojuego vj = juegosCoincidentes.getFirst(); // Si hay varios, toma el primero
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
        separarPlataformas(formateado, vj);
    }

    private void separarPlataformas(JuegoFormateado formateado, Videojuego vj) {
        String[] plataformas = formateado.plataforma().split(",");
        for (String plataformaNombre : plataformas) {
            String nombrePlataforma = plataformaNombre.trim();
            if (nombrePlataforma.isBlank()) continue;
            Plataforma plataforma = plataformaService.findOrCreateByNombre(nombrePlataforma, null);
            // Crear relación Pertenece
            PerteneceId perteneceId = new PerteneceId();
            perteneceId.setIdvideojuego(vj.getId());
            perteneceId.setIdplataforma(plataforma.getId());
            if (perteneceService.findById(perteneceId).isEmpty()) {
                Pertenece pertenece = new Pertenece();
                pertenece.setId(perteneceId);
                pertenece.setIdvideojuego(vj);
                pertenece.setIdplataforma(plataforma);
                perteneceService.save(pertenece);
            }
            // Crear Precio
            crearPrecio(vj, plataforma, formateado.precio());
        }
    }

    private void crearPrecio(Videojuego vj, Plataforma plataforma, BigDecimal precioValor) {
        Precio precio = new Precio();
        PrecioId precioId = new PrecioId();
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
