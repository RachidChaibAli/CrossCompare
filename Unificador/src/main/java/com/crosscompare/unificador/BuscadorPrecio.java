package com.crosscompare.unificador;

import com.crosscompare.unificador.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuscadorPrecio {
    @Autowired
    private BusquedaService busquedaService;
    @Autowired
    private PrecioService precioService;
    @Autowired
    private VideojuegoService videojuegoService;

    /**
     * Dada una búsqueda (término), devuelve una lista de PreciosJuego:
     * - Cada uno contiene el videojuego y un map de plataforma->precio
     */
    public ArrayList<PreciosJuego> obtenerPreciosPorBusqueda(String busqueda) {
        // Buscar todas las entradas de Busqueda con ese término
        List<Busqueda> busquedas = busquedaService.findByTermino(busqueda);
        // Obtener los IDs de videojuegos únicos
        Set<Integer> idsVideojuegos = new HashSet<>();
        for (Busqueda b : busquedas) {
            idsVideojuegos.add(b.getId().getIdvideojuego());
        }
        ArrayList<PreciosJuego> resultado = new ArrayList<>();
        for (Integer idVj : idsVideojuegos) {
            Optional<Videojuego> vjOpt = videojuegoService.findById(Long.valueOf(idVj));
            if (vjOpt.isEmpty()) continue;
            Videojuego vj = vjOpt.get();
            // Buscar todos los precios de este videojuego
            List<Precio> precios = precioService.findByIdVideojuego(idVj);
            // Map plataforma->precio (último precio por plataforma)
            Map<String, java.math.BigDecimal> preciosPorPlataforma = new HashMap<>();
            for (Precio precio : precios) {
                String plataforma = precio.getIdplataforma().getNombre();
                preciosPorPlataforma.put(plataforma, precio.getPrecio());
            }
            resultado.add(new PreciosJuego(vj, preciosPorPlataforma));
        }
        return resultado;
    }
}
