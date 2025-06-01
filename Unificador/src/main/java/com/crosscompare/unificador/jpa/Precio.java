package com.crosscompare.unificador.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "precio")
public class Precio {
    @EmbeddedId
    private PrecioId id;

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

    @org.springframework.lang.NonNull
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public PrecioId getId() {
        return id;
    }

    public void setId(PrecioId id) {
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

}