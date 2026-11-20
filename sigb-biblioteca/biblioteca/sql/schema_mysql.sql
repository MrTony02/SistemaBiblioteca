-- SIGB - Biblioteca Universitaria (Grupo 3)
-- Esquema para MySQL 8+ (InnoDB: llaves foráneas y transacciones ACID)
-- Ejecutar con:  mysql -u root -p < sql/schema_mysql.sql

CREATE DATABASE IF NOT EXISTS biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca;

DROP TABLE IF EXISTS multa;
DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS ejemplar;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    documento   VARCHAR(20)  NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE material (
    id_material INT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(200) NOT NULL,
    autor       VARCHAR(150)
) ENGINE = InnoDB;

CREATE TABLE ejemplar (
    id_ejemplar INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(20) NOT NULL UNIQUE,            -- p. ej. EJ-1094 (el que usa el bibliotecario)
    id_material INT         NOT NULL,
    estado      VARCHAR(12) NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT chk_ejemplar_estado CHECK (estado IN ('DISPONIBLE', 'PRESTADO', 'EXTRAVIADO')),
    CONSTRAINT fk_ejemplar_material FOREIGN KEY (id_material) REFERENCES material (id_material)
) ENGINE = InnoDB;

CREATE TABLE prestamo (
    id_prestamo      INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario       INT  NOT NULL,
    id_ejemplar      INT  NOT NULL,
    fecha_inicio     DATE NOT NULL,
    fecha_limite     DATE NOT NULL,                      -- RN-02: obligatoria
    fecha_devolucion DATE NULL,                          -- NULL = préstamo activo
    CONSTRAINT chk_prestamo_fechas CHECK (fecha_limite >= fecha_inicio),
    CONSTRAINT fk_prestamo_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuario (id_usuario),
    CONSTRAINT fk_prestamo_ejemplar FOREIGN KEY (id_ejemplar) REFERENCES ejemplar (id_ejemplar)
) ENGINE = InnoDB;

CREATE TABLE multa (
    id_multa    INT AUTO_INCREMENT PRIMARY KEY,
    id_prestamo INT           NOT NULL UNIQUE,           -- un préstamo tiene 0..1 multa
    valor       DECIMAL(10,2) NOT NULL,
    pagada      BOOLEAN       NOT NULL DEFAULT FALSE,    -- RN-03
    CONSTRAINT chk_multa_valor CHECK (valor >= 0),
    CONSTRAINT fk_multa_prestamo FOREIGN KEY (id_prestamo) REFERENCES prestamo (id_prestamo)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- Datos de ejemplo
-- ---------------------------------------------------------------------------
INSERT INTO usuario (nombre, documento) VALUES
    ('Ana Gómez',     '1020304050'),
    ('Carlos Ruiz',   '1098765432'),
    ('Laura Pérez',   '1012345678');

INSERT INTO material (titulo, autor) VALUES
    ('Introducción a Java',  'Herbert Schildt'),
    ('Bases de Datos SQL',   'Ramez Elmasri'),
    ('Lógica de Sistemas',   'Irving Copi'),
    ('Programación en Java', 'Paul Deitel');

INSERT INTO ejemplar (codigo, id_material, estado) VALUES
    ('EJ-1001', 1, 'DISPONIBLE'),
    ('EJ-1002', 1, 'DISPONIBLE'),
    ('EJ-1003', 2, 'PRESTADO'),      -- tiene el préstamo vencido de abajo
    ('EJ-1004', 3, 'DISPONIBLE'),
    ('EJ-1005', 4, 'DISPONIBLE'),
    ('EJ-1094', 4, 'EXTRAVIADO');

-- Préstamo vencido (límite hace 3 días): al devolver EJ-1003 se genera una multa de 3 días.
INSERT INTO prestamo (id_usuario, id_ejemplar, fecha_inicio, fecha_limite) VALUES
    (2, 3, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY));
