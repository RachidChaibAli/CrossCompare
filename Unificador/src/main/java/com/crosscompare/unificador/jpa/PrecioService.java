package com.crosscompare.unificador.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PrecioService {
    private final PrecioRepository precioRepository;

    @Autowired
    public PrecioService(PrecioRepository precioRepository) {
        this.precioRepository = precioRepository;
    }

    public List<Precio> findAll() {
        return precioRepository.findAll();
    }

    public Optional<Precio> findById(PrecioId id) {
        return precioRepository.findById(id);
    }

    public Precio save(Precio precio) {
        return precioRepository.save(precio);
    }

    public void deleteById(PrecioId id) {
        precioRepository.deleteById(id);
    }

    public List<Precio> findByIdVideojuego(Integer idVideojuego) {
        return precioRepository.findByIdvideojuego_Id(idVideojuego);
    }
}
