package com.crosscompare.unificador.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class PrecioId implements Serializable {
    private static final long serialVersionUID = 1530505595796015917L;
    @org.springframework.lang.NonNull
    @Column(name = "idvideojuego", nullable = false)
    private Integer idvideojuego;

    @org.springframework.lang.NonNull
    @Column(name = "idplataforma", nullable = false)
    private Integer idplataforma;

    @org.springframework.lang.NonNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public Integer getIdvideojuego() {
        return idvideojuego;
    }

    public void setIdvideojuego(Integer idvideojuego) {
        this.idvideojuego = idvideojuego;
    }

    public Integer getIdplataforma() {
        return idplataforma;
    }

    public void setIdplataforma(Integer idplataforma) {
        this.idplataforma = idplataforma;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PrecioId entity = (PrecioId) o;
        return Objects.equals(this.fecha, entity.fecha) &&
                Objects.equals(this.idplataforma, entity.idplataforma) &&
                Objects.equals(this.idvideojuego, entity.idvideojuego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, idplataforma, idvideojuego);
    }

}