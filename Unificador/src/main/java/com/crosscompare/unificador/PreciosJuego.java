package com.crosscompare.unificador;

import com.crosscompare.unificador.jpa.Videojuego;
import java.util.Map;

public record PreciosJuego(
    Videojuego videojuego,
    Map<String, java.math.BigDecimal> preciosPorPlataforma
) {}
