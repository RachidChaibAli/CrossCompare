package com.crosscompare.unificador.jpa;

import com.crosscompare.unificador.JuegoFormateado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class VideojuegoService {
    private final VideojuegoRepository videojuegoRepository;

    @Autowired
    public VideojuegoService(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    public List<Videojuego> findAll() {
        return videojuegoRepository.findAll();
    }

    public Optional<Videojuego> findById(Long id) {
        return videojuegoRepository.findById(id);
    }

    public Videojuego save(Videojuego videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    public void deleteById(Long id) {
        videojuegoRepository.deleteById(id);
    }

    public List<Videojuego> buscarJuegosCoincidentes(JuegoFormateado juego) {
        List<Videojuego> candidatos = videojuegoRepository.findByNombre(juego.nombre());
        List<Videojuego> coincidencias = new ArrayList<>();
        for (Videojuego v : candidatos) {
            boolean fechaCoincide = false;
            // Si alguna de las fechas es null o vacía, se consideran iguales
            if (v.getFechalanzamiento() == null || juego.fechaLanzamiento() == null) {
                fechaCoincide = true;
            } else {
                long diff = Math.abs(v.getFechalanzamiento().getYear() - juego.fechaLanzamiento().getYear());
                fechaCoincide = diff <= 1;
            }
            if (fechaCoincide) {
                coincidencias.add(v);
            }
        }
        return coincidencias;
    }
}
