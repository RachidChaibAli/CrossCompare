package com.crosscompare.unificador.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PerteneceId implements Serializable {
    private static final long serialVersionUID = -5314992300466936677L;
    @NonNull
    @Column(name = "idvideojuego", nullable = false)
    private Integer idvideojuego;

    @NonNull
    @Column(name = "idplataforma", nullable = false)
    private Integer idplataforma;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PerteneceId entity = (PerteneceId) o;
        return Objects.equals(this.idplataforma, entity.idplataforma) &&
                Objects.equals(this.idvideojuego, entity.idvideojuego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idplataforma, idvideojuego);
    }

}