package com.app;

import com.app.config.AppConfig;
import com.app.controller.UsuarioController;
import com.app.controller.ProductoController;
import com.app.dao.UsuarioDAO;
import com.app.dao.ProductoDAO;
import com.app.dao.impl.UsuarioDAOImpl;
import com.app.dao.impl.ProductoDAOImpl;
import com.app.view.ConsoleView;
import com.app.view.SwingView;
import com.app.view.View;

import java.util.Scanner;
import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.getInstance();
        Scanner scanner = new Scanner(System.in);

        // ── Menú principal para elegir tipo de vista ──
        System.out.println("╔═══ " + config.getAppName() + " ═══╗");
        System.out.println("  1. Consola");
        System.out.println("  2. Swing (GUI)");
        System.out.println("╚" + "═".repeat(config.getAppName().length() + 8) + "╝");
        System.out.print("Elige tipo de vista: ");

        int viewChoice;
        try {
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                viewChoice = 1; // Por defecto consola
            } else {
                viewChoice = Integer.parseInt(input.trim());
            }
        } catch (NumberFormatException | NoSuchElementException e) {
            viewChoice = 1; // Por defecto consola
        }

        // ── Factory: elige la vista según selección ──
        View view = createView(viewChoice);

        // ── Menú para elegir módulo ──
        System.out.println("\n╔═══ Seleccionar Módulo ═══╗");
        System.out.println("  1. Gestión de Usuarios");
        System.out.println("  2. Gestión de Productos");
        System.out.println("  3. Salir");
        System.out.println("╚" + "═".repeat(23) + "╝");
        System.out.print("Elige módulo: ");

        int moduleChoice;
        try {
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                moduleChoice = 3; // Salir por defecto
            } else {
                moduleChoice = Integer.parseInt(input.trim());
            }
        } catch (NumberFormatException | NoSuchElementException e) {
            moduleChoice = 3; // Salir por defecto
        }

        view.showMessage("Bienvenido a " + config.getAppName());

        // ── Inyección de dependencias y ejecución ──
        switch (moduleChoice) {
            case 1 -> {
                UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
                UsuarioController usuarioController = new UsuarioController(view, usuarioDAO);
                usuarioController.run();
            }
            case 2 -> {
                ProductoDAO productoDAO = new ProductoDAOImpl();
                ProductoController productoController = new ProductoController(view, productoDAO);
                productoController.run();
            }
            case 3 -> view.showMessage("¡Hasta luego!");
            default -> view.showError("Opción no válida. Saliendo...");
        }

        scanner.close();
    }

    private static View createView(int choice) {
        return switch (choice) {
            case 2 -> new SwingView();
            default -> new ConsoleView();
        };
    }
}