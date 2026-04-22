package com.app.view;

import com.app.model.entity.Usuario;
import com.app.model.entity.Producto;
import java.util.List;

/**
 * Comportamiento común a todas las vistas.
 * Evita duplicar lógica de formato entre ConsoleView y SwingView.
 */
public abstract class BaseView implements View {

    // ── Formato compartido para la lista de usuarios ──
    protected String formatUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) return "(Sin usuarios)";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-30s%n", "ID", "NOMBRE", "EMAIL"));
        sb.append("─".repeat(55)).append("\n");
        for (Usuario u : usuarios) {
            sb.append(String.format("%-5d %-20s %-30s%n",
                    u.getId(), u.getNombre(), u.getEmail()));
        }
        return sb.toString();
    }

    protected String formatUsuario(Usuario u) {
        return String.format("ID: %d%nNombre: %s%nEmail: %s",
                u.getId(), u.getNombre(), u.getEmail());
    }

    // ── Formato compartido para la lista de productos ──
    protected String formatProductos(List<Producto> productos) {
        if (productos.isEmpty()) return "(Sin productos)";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-25s %-30s %-10s %-8s%n", "ID", "NOMBRE", "DESCRIPCIÓN", "PRECIO", "STOCK"));
        sb.append("─".repeat(85)).append("\n");
        for (Producto p : productos) {
            sb.append(String.format("%-5d %-25s %-30s %-10.2f %-8d%n",
                    p.getId(), 
                    p.getNombre().length() > 24 ? p.getNombre().substring(0, 21) + "..." : p.getNombre(),
                    p.getDescripcion().length() > 29 ? p.getDescripcion().substring(0, 26) + "..." : p.getDescripcion(),
                    p.getPrecio(),
                    p.getStock()));
        }
        return sb.toString();
    }

    protected String formatProducto(Producto p) {
        return String.format("ID: %d%nNombre: %s%nDescripción: %s%nPrecio: %.2f%nStock: %d",
                p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock());
    }

    protected String buildMenu(String[] options, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══ ").append(title).append(" ═══╗\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(String.format("  %d. %s%n", i + 1, options[i]));
        }
        sb.append("╚" + "═".repeat(title.length() + 8) + "╝");
        return sb.toString();
    }
}