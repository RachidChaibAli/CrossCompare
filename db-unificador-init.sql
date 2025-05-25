-- Script de inicialización para db-unificador
CREATE SCHEMA IF NOT EXISTS `db-unificador`;
USE `db-unificador`;

CREATE TABLE IF NOT EXISTS `videojuego` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(255) NOT NULL,
  `fechalanzamiento` DATE,
  `desarrolladora` VARCHAR(255),
  `descripcion` TEXT,
  `boxart_url` VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS `plataforma` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(255) NOT NULL,
  `fechalanzamiento` DATE
);

CREATE TABLE IF NOT EXISTS `precio` (
  `idvideojuego` INT NOT NULL,
  `idplataforma` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`idvideojuego`, `idplataforma`, `fecha`),
  FOREIGN KEY (`idvideojuego`) REFERENCES `videojuego`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`idplataforma`) REFERENCES `plataforma`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `pertenece` (
  `idvideojuego` INT NOT NULL,
  `idplataforma` INT NOT NULL,
  PRIMARY KEY (`idvideojuego`, `idplataforma`),
  FOREIGN KEY (`idvideojuego`) REFERENCES `videojuego`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`idplataforma`) REFERENCES `plataforma`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `busqueda` (
  `idvideojuego` INT NOT NULL,
  `termino` VARCHAR(255) NOT NULL,
  `fecha` DATETIME NOT NULL,
  PRIMARY KEY (`idvideojuego`, `termino`),
  FOREIGN KEY (`idvideojuego`) REFERENCES `videojuego`(`id`) ON DELETE CASCADE
);
