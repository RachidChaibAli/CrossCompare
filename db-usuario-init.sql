-- Script de inicialización para db-usuario
CREATE SCHEMA IF NOT EXISTS db-unificador;
USE db-unificador;

CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    fechabaja DATE
);

CREATE TABLE IF NOT EXISTS favorito (
    idusuario INT NOT NULL,
    idvideojuego INT NOT NULL,
    PRIMARY KEY (idusuario, idvideojuego),
    FOREIGN KEY (idusuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (idvideojuego) REFERENCES videojuego(id) ON DELETE CASCADE
);
