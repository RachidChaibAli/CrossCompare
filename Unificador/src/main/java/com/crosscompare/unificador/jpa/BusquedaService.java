package com.crosscompare.unificador.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BusquedaService {
    private final BusquedaRepository busquedaRepository;

    @Autowired
    public BusquedaService(BusquedaRepository busquedaRepository) {
        this.busquedaRepository = busquedaRepository;
    }

    public List<Busqueda> findAll() {
        return busquedaRepository.findAll();
    }

    public Optional<Busqueda> findById(BusquedaId id) {
        return busquedaRepository.findById(id);
    }

    public Busqueda save(Busqueda busqueda) {
        return busquedaRepository.save(busqueda);
    }

    public void deleteById(BusquedaId id) {
        busquedaRepository.deleteById(id);
    }

    public List<Busqueda> findByTermino(String termino) {
        return busquedaRepository.findById_TerminoIgnoreCase(termino);
    }
}
