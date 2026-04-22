-- ============================================================
-- Schema completo — Usuarios + Productos
-- ============================================================


-- ==========================================
-- PostgreSQL Schema
-- ==========================================
-- 1. Crear la base de datos manualmente:
--    CREATE DATABASE appdb;
-- 2. Conectarse: \c appdb

-- Tabla de usuarios (existente, sin cambios)
CREATE TABLE IF NOT EXISTS usuarios (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE
);
CREATE INDEX IF NOT EXISTS idx_usuarios_nombre ON usuarios(nombre);

-- Tabla de productos (nueva)
-- NUMERIC(10,2): hasta 10 dígitos totales, 2 decimales — estándar para precios
CREATE TABLE IF NOT EXISTS productos (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio      NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    stock       INT           NOT NULL DEFAULT 0 CHECK (stock >= 0)
);
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);


-- ==========================================
-- MySQL Schema (Referencia)
-- ==========================================
-- CREATE DATABASE IF NOT EXISTS appdb;
-- USE appdb;

-- -- Tabla de usuarios (existente, sin cambios)
-- CREATE TABLE IF NOT EXISTS usuarios (
--     id      INT AUTO_INCREMENT PRIMARY KEY,
--     nombre  VARCHAR(100) NOT NULL,
--     email   VARCHAR(150) NOT NULL UNIQUE,
--     INDEX idx_usuarios_nombre (nombre)
-- );

-- -- Tabla de productos (nueva)
-- -- DECIMAL(10,2) es el equivalente MySQL de NUMERIC(10,2) en PostgreSQL
-- CREATE TABLE IF NOT EXISTS productos (
--     id          INT AUTO_INCREMENT PRIMARY KEY,
--     nombre      VARCHAR(150)   NOT NULL,
--     descripcion TEXT,
--     precio      DECIMAL(10, 2) NOT NULL CHECK (precio >= 0),
--     stock       INT            NOT NULL DEFAULT 0 CHECK (stock >= 0),
--     INDEX idx_productos_nombre (nombre)
-- );
