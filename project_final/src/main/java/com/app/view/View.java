package com.app.view;

import com.app.model.entity.Usuario;
import com.app.model.entity.Producto;
import java.util.List;

/**
 * Contrato que toda vista debe cumplir.
 * El Controller solo conoce esta interfaz.
 */
public interface View {
    void       showMessage(String msg);
    void       showError(String msg);
    void       showUsuarios(List<Usuario> usuarios);
    void       showUsuario(Usuario usuario);
    void       showProductos(List<Producto> productos);
    void       showProducto(Producto producto);
    String     askInput(String prompt);
    boolean    confirm(String question);
    void       showMenu(String[] options, String title);
    int        getMenuChoice();
    int        getProductMenuChoice();
}