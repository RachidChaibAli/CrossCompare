package com.crosscompare.unificador.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import java.time.LocalDate;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "videojuego")
public class Videojuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @jakarta.validation.constraints.Size(max = 255)
    @org.springframework.lang.NonNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "fechalanzamiento")
    private LocalDate fechalanzamiento;

    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "desarrolladora")
    private String desarrolladora;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @jakarta.validation.constraints.Size(max = 512)
    @Column(name = "boxart_url", length = 512)
    private String boxartUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechalanzamiento() {
        return fechalanzamiento;
    }

    public void setFechalanzamiento(LocalDate fechalanzamiento) {
        this.fechalanzamiento = fechalanzamiento;
    }

    public String getDesarrolladora() {
        return desarrolladora;
    }

    public void setDesarrolladora(String desarrolladora) {
        this.desarrolladora = desarrolladora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getBoxartUrl() {
        return boxartUrl;
    }

    public void setBoxartUrl(String boxartUrl) {
        this.boxartUrl = boxartUrl;
    }

}