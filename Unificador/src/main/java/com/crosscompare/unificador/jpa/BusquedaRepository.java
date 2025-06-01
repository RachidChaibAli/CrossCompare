package com.crosscompare.unificador.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusquedaRepository extends JpaRepository<Busqueda, BusquedaId> {
    List<Busqueda> findById_TerminoIgnoreCase(String termino);
}
