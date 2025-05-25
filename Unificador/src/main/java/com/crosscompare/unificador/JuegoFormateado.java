package com.crosscompare.unificador;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JuegoFormateado(
    String nombre,
    String descripcion,
    LocalDate fechaLanzamiento,
    String desarrollador,
    String plataforma,
    BigDecimal precio,
    String valoracion,
    String boxartUrl,
    String busqueda
) {}
