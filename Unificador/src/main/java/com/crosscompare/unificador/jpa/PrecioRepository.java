package com.crosscompare.unificador.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrecioRepository extends JpaRepository<Precio, PrecioId> {
    List<Precio> findByIdvideojuego_Id(Integer idvideojuego);
}
