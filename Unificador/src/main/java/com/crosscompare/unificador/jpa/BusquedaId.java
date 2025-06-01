package com.crosscompare.unificador.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BusquedaId implements Serializable {
    private static final long serialVersionUID = -6919452202219172631L;
    @NotNull
    @Column(name = "idvideojuego", nullable = false)
    private Integer idvideojuego;

    @Size(max = 255)
    @NotNull
    @Column(name = "termino", nullable = false)
    private String termino;

    public Integer getIdvideojuego() {
        return idvideojuego;
    }

    public void setIdvideojuego(Integer idvideojuego) {
        this.idvideojuego = idvideojuego;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BusquedaId entity = (BusquedaId) o;
        return Objects.equals(this.termino, entity.termino) &&
                Objects.equals(this.idvideojuego, entity.idvideojuego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termino, idvideojuego);
    }

}