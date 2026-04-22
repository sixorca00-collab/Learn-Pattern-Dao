package com.app.controller;

import com.app.dao.ProductoDAO;
import com.app.model.entity.Producto;
import com.app.view.View;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de productos.
 * Recibe eventos de la Vista, orquesta el DAO y devuelve resultados a la Vista.
 * No sabe si la Vista es consola o Swing → depende de la interfaz View.
 */
public class ProductoController {

    private final View        view;
    private final ProductoDAO productoDAO;

    // Inyección de dependencias por constructor
    public ProductoController(View view, ProductoDAO productoDAO) {
        this.view        = view;
        this.productoDAO = productoDAO;
    }

    // ── Menú principal ──
    public void run() {
        String[] menuOptions = {
            "Listar todos", "Buscar por ID", "Buscar por nombre", "Buscar por rango de precio",
            "Crear producto", "Actualizar producto", "Eliminar producto", "Listar disponibles", "Salir"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestión de Productos");
            int choice = view.getProductMenuChoice();

            switch (choice) {
                case 1 -> listarTodos();
                case 2 -> buscarPorId();
                case 3 -> buscarPorNombre();
                case 4 -> buscarPorRangoPrecio();
                case 5 -> crearProducto();
                case 6 -> actualizarProducto();
                case 7 -> eliminarProducto();
                case 8 -> listarDisponibles();
                case 9 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
        view.showMessage("¡Hasta luego!");
    }

    // ── Operaciones CRUD ──

    public void listarTodos() {
        List<Producto> productos = productoDAO.findAll();
        if (productos.isEmpty()) {
            view.showMessage("No hay productos registrados.");
        } else {
            view.showProductos(productos);
        }
    }

    public void buscarPorId() {
        String input = view.askInput("ID del producto");
        try {
            int id = Integer.parseInt(input);
            Optional<Producto> producto = productoDAO.findById(id);
            if (producto.isPresent()) {
                view.showProducto(producto.get());
            } else {
                view.showError("No se encontró producto con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    public void buscarPorNombre() {
        String nombre = view.askInput("Nombre o parte del nombre a buscar");
        if (nombre.isBlank()) {
            view.showError("El nombre de búsqueda no puede estar vacío.");
            return;
        }
        
        List<Producto> productos = productoDAO.findByNombre(nombre);
        if (productos.isEmpty()) {
            view.showMessage("No se encontraron productos con ese nombre.");
        } else {
            view.showMessage("Se encontraron " + productos.size() + " producto(s):");
            view.showProductos(productos);
        }
    }

    public void buscarPorRangoPrecio() {
        String minInput = view.askInput("Precio mínimo");
        String maxInput = view.askInput("Precio máximo");
        
        try {
            BigDecimal min = new BigDecimal(minInput);
            BigDecimal max = new BigDecimal(maxInput);
            
            if (min.compareTo(max) > 0) {
                view.showError("El precio mínimo no puede ser mayor al máximo.");
                return;
            }
            
            List<Producto> productos = productoDAO.findByRangoPrecio(min, max);
            if (productos.isEmpty()) {
                view.showMessage("No se encontraron productos en ese rango de precio.");
            } else {
                view.showMessage("Se encontraron " + productos.size() + " producto(s):");
                view.showProductos(productos);
            }
        } catch (NumberFormatException e) {
            view.showError("Precios inválidos. Use formato numérico (ej: 19.99).");
        }
    }

    public void crearProducto() {
        String nombre = view.askInput("Nombre del producto");
        String descripcion = view.askInput("Descripción del producto");
        String precioInput = view.askInput("Precio del producto");
        String stockInput = view.askInput("Stock inicial");

        if (nombre.isBlank() || descripcion.isBlank()) {
            view.showError("Nombre y descripción son requeridos.");
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(precioInput);
            int stock = Integer.parseInt(stockInput);
            
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                view.showError("El precio debe ser mayor a cero.");
                return;
            }
            
            if (stock < 0) {
                view.showError("El stock no puede ser negativo.");
                return;
            }
            
            if (productoDAO.existsByNombre(nombre)) {
                view.showError("Ya existe un producto con ese nombre.");
                return;
            }

            Producto nuevo = new Producto(0, nombre, descripcion, precio, stock);
            productoDAO.save(nuevo);
            view.showMessage("Producto creado con ID: " + nuevo.getId());
            
        } catch (NumberFormatException e) {
            view.showError("Precio o stock inválidos.");
        }
    }

    public void actualizarProducto() {
        String input = view.askInput("ID del producto a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Producto> opt = productoDAO.findById(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró producto con ID " + id);
                return;
            }
            
            Producto p = opt.get();
            String nombre = view.askInput("Nuevo nombre [" + p.getNombre() + "]");
            String descripcion = view.askInput("Nueva descripción [" + p.getDescripcion() + "]");
            String precioInput = view.askInput("Nuevo precio [" + p.getPrecio() + "]");
            String stockInput = view.askInput("Nuevo stock [" + p.getStock() + "]");

            if (!nombre.isBlank()) p.setNombre(nombre);
            if (!descripcion.isBlank()) p.setDescripcion(descripcion);
            
            if (!precioInput.isBlank()) {
                try {
                    BigDecimal precio = new BigDecimal(precioInput);
                    if (precio.compareTo(BigDecimal.ZERO) > 0) {
                        p.setPrecio(precio);
                    } else {
                        view.showError("El precio debe ser mayor a cero.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    view.showError("Precio inválido.");
                    return;
                }
            }
            
            if (!stockInput.isBlank()) {
                try {
                    int stock = Integer.parseInt(stockInput);
                    if (stock >= 0) {
                        p.setStock(stock);
                    } else {
                        view.showError("El stock no puede ser negativo.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    view.showError("Stock inválido.");
                    return;
                }
            }

            boolean ok = productoDAO.update(p);
            view.showMessage(ok ? "Producto actualizado." : "No se pudo actualizar.");
            
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    public void eliminarProducto() {
        String input = view.askInput("ID del producto a eliminar");
        try {
            int id = Integer.parseInt(input);
            Optional<Producto> opt = productoDAO.findById(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró producto con ID " + id);
                return;
            }
            
            Producto p = opt.get();
            if (view.confirm("¿Confirmar eliminación del producto '" + p.getNombre() + "' (ID " + id + ")?")) {
                boolean ok = productoDAO.deleteById(id);
                view.showMessage(ok ? "Producto eliminado." : "No se pudo eliminar.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    public void listarDisponibles() {
        List<Producto> productos = productoDAO.findDisponibles();
        if (productos.isEmpty()) {
            view.showMessage("No hay productos disponibles con stock.");
        } else {
            view.showMessage("Productos disponibles con stock:");
            view.showProductos(productos);
        }
    }
}
