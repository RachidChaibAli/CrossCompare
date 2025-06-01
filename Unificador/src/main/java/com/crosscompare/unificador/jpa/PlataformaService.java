package com.crosscompare.unificador.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class PlataformaService {
    private final PlataformaRepository plataformaRepository;

    @Autowired
    public PlataformaService(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    public List<Plataforma> findAll() {
        return plataformaRepository.findAll();
    }

    public Optional<Plataforma> findById(Long id) {
        return plataformaRepository.findById(id);
    }

    public Plataforma save(Plataforma plataforma) {
        return plataformaRepository.save(plataforma);
    }

    public void deleteById(Long id) {
        plataformaRepository.deleteById(id);
    }

    public Plataforma findOrCreateByNombre(String nombre, LocalDate fechaLanzamiento) {
        List<Plataforma> plataformas = plataformaRepository.findByNombre(nombre);
        if (!plataformas.isEmpty()) {
            return plataformas.get(0);
        }
        Plataforma nueva = new Plataforma();
        nueva.setNombre(nombre);
        nueva.setFechalanzamiento(fechaLanzamiento);
        return plataformaRepository.save(nueva);
    }
}
