package com.crosscompare.unificador.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
    List<Plataforma> findByNombre(String nombre);
}
