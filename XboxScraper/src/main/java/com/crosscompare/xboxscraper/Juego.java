package com.crosscompare.xboxscraper;

public record Juego(
        String nombre,
        String descripcion,
        String fechaLanzamiento,
        String desarrollador,
        String plataforma,
        String precio,
        String valoracion
) {
}
