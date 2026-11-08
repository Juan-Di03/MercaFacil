package com.mercafacil.crud;

import com.mercafacil.crud.controller.AlmacenController;
import com.mercafacil.crud.dao.AlmacenDAO;
import com.mercafacil.crud.view.AlmacenView;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, continúa con la apariencia por defecto de Java
        }

        // Ejecutar la interfaz gráfica en el Event Dispatch Thread (EDT) de Swing
        java.awt.EventQueue.invokeLater(() -> {
            AlmacenView vista = new AlmacenView();
            AlmacenDAO dao = new AlmacenDAO();
            new AlmacenController(vista, dao);
            vista.setVisible(true);
        });
    }
}
