package com.app.model.entity;

import java.math.BigDecimal;

//Nueva entidad producto
public class Producto {

    // ── Campos ──
    private int        id;
    private String     nombre;
    private String     descripcion;
    private BigDecimal precio;
    private int        stock;

    // Constructor vacío requerido para el mapeo desde ResultSet
    public Producto() {}

    // Constructor completo para crear instancias con todos los datos
    public Producto(int id, String nombre, String descripcion,
                    BigDecimal precio, int stock) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.precio      = precio;
        this.stock       = stock;
    }


    public int getId() { return id; }


    public String getNombre() { return nombre; }


    public String getDescripcion() { return descripcion; }

    public BigDecimal getPrecio() { return precio; }


    public int getStock() { return stock; }



    public void setId(int id) { this.id = id; }


    public void setNombre(String nombre) { this.nombre = nombre; }


    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


    public void setPrecio(BigDecimal precio) { this.precio = precio; }


    public void setStock(int stock) { this.stock = stock; }


    @Override
    public String toString() {
        return String.format(
            "Producto{id=%d, nombre='%s', precio=%s, stock=%d}",
            id, nombre, precio, stock
        );
    }
}
