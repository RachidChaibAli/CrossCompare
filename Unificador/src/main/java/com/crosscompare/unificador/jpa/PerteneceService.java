package com.crosscompare.unificador.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PerteneceService {
    private final PerteneceRepository perteneceRepository;

    @Autowired
    public PerteneceService(PerteneceRepository perteneceRepository) {
        this.perteneceRepository = perteneceRepository;
    }

    public List<Pertenece> findAll() {
        return perteneceRepository.findAll();
    }

    public Optional<Pertenece> findById(PerteneceId id) {
        return perteneceRepository.findById(id);
    }

    public Pertenece save(Pertenece pertenece) {
        return perteneceRepository.save(pertenece);
    }

    public void deleteById(PerteneceId id) {
        perteneceRepository.deleteById(id);
    }
}
