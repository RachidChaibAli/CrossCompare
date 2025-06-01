package com.crosscompare.unificador.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    List<Videojuego> findByNombre(String nombre);
}
