package com.app.dao.impl;

import com.app.dao.ProductoDAO;
import com.app.model.entity.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta de ProductoDAO.
 * Hereda toda la lógica CRUD genérica de GenericDAOImpl;
 * aquí solo se define el SQL y el mapeo fila → objeto.
 */
public class ProductoDAOImpl extends GenericDAOImpl<Producto, Integer>
        implements ProductoDAO {

    // ── Constantes SQL ─────────────────────────────────────────────────────────

    // Inserta un nuevo producto; id es generado automáticamente por la BD
    private static final String INSERT =
        "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";

    // Actualiza todos los campos modificables por ID
    private static final String UPDATE =
        "UPDATE productos SET nombre=?, descripcion=?, precio=?, stock=? WHERE id=?";

    // Elimina el registro por su clave primaria
    private static final String DELETE =
        "DELETE FROM productos WHERE id=?";

    // Recupera un único producto por su ID
    private static final String FIND_BY_ID =
        "SELECT * FROM productos WHERE id=?";

    // Recupera todos los productos sin filtro
    private static final String FIND_ALL =
        "SELECT * FROM productos";

    // Busca productos cuyo nombre contenga el texto (insensible a mayúsculas vía LIKE)
    private static final String FIND_BY_NOMBRE =
        "SELECT * FROM productos WHERE nombre LIKE ?";

    // Filtra productos cuyo precio esté dentro de un rango inclusivo
    private static final String FIND_BY_RANGO =
        "SELECT * FROM productos WHERE precio BETWEEN ? AND ?";

    // Cuenta registros con ese nombre exacto para verificar existencia
    private static final String EXISTS_NOMBRE =
        "SELECT COUNT(*) FROM productos WHERE nombre=?";

    // Devuelve solo productos con unidades disponibles en inventario
    private static final String FIND_DISPONIBLES =
        "SELECT * FROM productos WHERE stock > 0";

    // ── Mapeo ResultSet → Entidad ──────────────────────────────────────────────

    // Convierte una fila de la BD en un objeto Producto
    @Override
    protected Producto mapRow(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBigDecimal("precio"),   // BigDecimal preserva precisión monetaria
            rs.getInt("stock")
        );
    }

    // ── Provisión de SQL al padre (GenericDAOImpl) ─────────────────────────────

    // Cada getter le entrega al padre la query correcta para esta entidad
    @Override protected String getInsertSQL()   { return INSERT; }
    @Override protected String getUpdateSQL()   { return UPDATE; }
    @Override protected String getDeleteSQL()   { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL()  { return FIND_ALL; }

    // ── Binding de parámetros ──────────────────────────────────────────────────

    // Vincula los campos del objeto al PreparedStatement de INSERT
    @Override
    protected void setInsertParams(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setBigDecimal(3, p.getPrecio());
        ps.setInt(4, p.getStock());
    }

    // Vincula los campos del objeto al PreparedStatement de UPDATE
    @Override
    protected void setUpdateParams(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setBigDecimal(3, p.getPrecio());
        ps.setInt(4, p.getStock());
        ps.setInt(5, p.getId());         // WHERE id=? va al final
    }

    // Vincula el ID al PreparedStatement de DELETE
    @Override
    protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    // Vincula el ID al PreparedStatement de SELECT por ID
    @Override
    protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    // ── save: INSERT con recuperación del ID generado ──────────────────────────

    /**
     * Persiste el producto y actualiza su ID con la clave generada por la BD.
     * Usa Statement.RETURN_GENERATED_KEYS para obtener el ID sin una consulta extra.
     */
    @Override
    public Producto save(Producto p) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, p);
            ps.executeUpdate();

            // Recupera el ID auto-generado y lo asigna al objeto devuelto
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error en save(Producto)", e);
        }
    }

    // ── Métodos específicos de ProductoDAO ─────────────────────────────────────

    // Búsqueda parcial por nombre usando comodines LIKE %texto%
    @Override
    public List<Producto> findByNombre(String nombre) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NOMBRE)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByNombre", e);
        }
        return list;
    }

    // Filtra productos dentro de un rango de precios usando BETWEEN
    @Override
    public List<Producto> findByRangoPrecio(BigDecimal min, BigDecimal max) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_RANGO)) {

            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByRangoPrecio", e);
        }
        return list;
    }

    // Retorna true si COUNT(*) > 0 para ese nombre exacto
    @Override
    public boolean existsByNombre(String nombre) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_NOMBRE)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsByNombre", e);
        }
    }

    // Devuelve productos con stock > 0, útil para mostrar catálogo disponible
    @Override
    public List<Producto> findDisponibles() {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_DISPONIBLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error en findDisponibles", e);
        }
        return list;
    }
}
