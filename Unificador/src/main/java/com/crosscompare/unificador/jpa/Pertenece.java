package com.crosscompare.unificador.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "pertenece")
public class Pertenece {
    @EmbeddedId
    private PerteneceId id;

    @MapsId("idvideojuego")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idvideojuego", nullable = false)
    private Videojuego idvideojuego;

    @MapsId("idplataforma")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idplataforma", nullable = false)
    private Plataforma idplataforma;

    public PerteneceId getId() {
        return id;
    }

    public void setId(PerteneceId id) {
        this.id = id;
    }

    public Videojuego getIdvideojuego() {
        return idvideojuego;
    }

    public void setIdvideojuego(Videojuego idvideojuego) {
        this.idvideojuego = idvideojuego;
    }

    public Plataforma getIdplataforma() {
        return idplataforma;
    }

    public void setIdplataforma(Plataforma idplataforma) {
        this.idplataforma = idplataforma;
    }

}