package com.crosscompare.playstationscraper;

public record Juego(
        String nombre,
        String descripcion,
        String fechaLanzamiento,
        String desarrollador,
        String plataforma,
        String precio,
        String valoracion,
        String boxartUrl,
        String busqueda
) {
}
