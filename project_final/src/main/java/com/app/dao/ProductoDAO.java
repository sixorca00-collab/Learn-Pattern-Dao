package com.app.dao;

import com.app.model.entity.Producto;
import java.math.BigDecimal;
import java.util.List;


public interface ProductoDAO extends GenericDAO<Producto, Integer> {

    // Busca productos cuyo nombre contenga el texto dado (búsqueda parcial)
    List<Producto> findByNombre(String nombre);

    // Devuelve productos cuyo precio esté dentro del rango indicado
    List<Producto> findByRangoPrecio(BigDecimal min, BigDecimal max);

    // Verifica si existe al menos un producto con ese nombre exacto
    boolean existsByNombre(String nombre);

    // Devuelve solo los productos que tienen stock mayor a cero
    List<Producto> findDisponibles();
}
