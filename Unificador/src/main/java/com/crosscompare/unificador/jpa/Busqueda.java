package com.crosscompare.unificador.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "busqueda")
public class Busqueda {
    @EmbeddedId
    private BusquedaId id;

    @MapsId("idvideojuego")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idvideojuego", nullable = false)
    private Videojuego idvideojuego;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    public BusquedaId getId() {
        return id;
    }

    public void setId(BusquedaId id) {
        this.id = id;
    }

    public Videojuego getIdvideojuego() {
        return idvideojuego;
    }

    public void setIdvideojuego(Videojuego idvideojuego) {
        this.idvideojuego = idvideojuego;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

}