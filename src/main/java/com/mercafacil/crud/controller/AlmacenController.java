package com.mercafacil.crud.controller;

import com.mercafacil.crud.dao.AlmacenDAO;
import com.mercafacil.crud.model.AlmacenModel;
import com.mercafacil.crud.view.AlmacenView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Controlador para la gestión de Almacenes.
 * Coordina las operaciones del CRUD (Crear, Leer/Listar, Buscar, Actualizar, Eliminar)
 * entre la interfaz gráfica AlmacenView y el acceso a datos AlmacenDAO.
 */
public class AlmacenController {
    private final AlmacenView vista;
    private final AlmacenDAO dao;

    public AlmacenController(AlmacenView vista, AlmacenDAO dao) {
        this.vista = vista;
        this.dao = dao;

        // Listeners para botones CRUD
        this.vista.btnGuardar.addActionListener(e -> guardar());
        this.vista.btnActualizar.addActionListener(e -> actualizar());
        this.vista.btnEliminar.addActionListener(e -> eliminar());
        this.vista.btnLimpiar.addActionListener(e -> limpiarCampos());

        // Listeners para Búsqueda
        this.vista.btnBuscar.addActionListener(e -> buscar());
        this.vista.btnRefrescar.addActionListener(e -> {
            this.vista.txtBuscar.setText("");
            listar();
        });
        this.vista.txtBuscar.addActionListener(e -> buscar()); // Permite presionar Enter en la caja de búsqueda

        // Evento al seleccionar una fila de la tabla para cargar datos en el formulario
        this.vista.tablaAlmacenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        // Cargar todos los almacenes al iniciar
        listar();
    }

    /**
     * READ / LISTAR: Carga y muestra todos los registros en la tabla.
     */
    private void listar() {
        DefaultTableModel model = (DefaultTableModel) vista.tablaAlmacenes.getModel();
        model.setRowCount(0); // Limpiar tabla

        List<AlmacenModel> lista = dao.listar();
        for (AlmacenModel a : lista) {
            model.addRow(new Object[]{
                a.getCodigo_almacen(),
                a.getNombre(),
                a.getDireccion() != null ? a.getDireccion() : "",
                a.getNombre_gerente() != null ? a.getNombre_gerente() : ""
            });
        }
    }

    /**
     * BUSCAR: Filtra los registros según el texto ingresado (código, nombre, dirección o gerente).
     */
    private void buscar() {
        String criterio = vista.txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            listar();
            return;
        }

        List<AlmacenModel> resultados = dao.buscar(criterio);
        DefaultTableModel model = (DefaultTableModel) vista.tablaAlmacenes.getModel();
        model.setRowCount(0);

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "No se encontraron almacenes que coincidan con: \"" + criterio + "\"",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (AlmacenModel a : resultados) {
                model.addRow(new Object[]{
                    a.getCodigo_almacen(),
                    a.getNombre(),
                    a.getDireccion() != null ? a.getDireccion() : "",
                    a.getNombre_gerente() != null ? a.getNombre_gerente() : ""
                });
            }
        }
    }

    /**
     * CREATE: Registra un nuevo almacén.
     * El usuario no digita el código, el sistema lo genera automáticamente mediante la secuencia de la base de datos.
     */
    private void guardar() {
        String nombre = vista.txtNombre.getText().trim();
        String direccion = vista.txtDireccion.getText().trim();
        String gerente = vista.txtGerente.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "El Nombre del almacén es obligatorio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            vista.txtNombre.requestFocus();
            return;
        }

        AlmacenModel nuevo = new AlmacenModel(nombre, direccion, gerente);
        if (dao.insertar(nuevo)) {
            JOptionPane.showMessageDialog(vista,
                    "Almacén registrado con éxito.\nCódigo autogenerado: " + nuevo.getCodigo_almacen(),
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            listar();
            limpiarCampos();
        } else {
            String error = dao.getUltimoError();
            if (error == null || error.isBlank()) {
                error = "No fue posible guardar el almacén.";
            }
            JOptionPane.showMessageDialog(vista, error, "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * UPDATE: Actualiza los datos del almacén seleccionado en la tabla.
     */
    private void actualizar() {
        String strCodigo = vista.txtCodigo.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String direccion = vista.txtDireccion.getText().trim();
        String gerente = vista.txtGerente.getText().trim();

        if (strCodigo.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un almacén de la tabla para poder actualizarlo.",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "El Nombre del almacén no puede estar vacío.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            vista.txtNombre.requestFocus();
            return;
        }

        int codigo = Integer.parseInt(strCodigo);
        AlmacenModel almacen = new AlmacenModel(codigo, nombre, direccion, gerente);

        if (dao.actualizar(almacen)) {
            JOptionPane.showMessageDialog(vista,
                    "Almacén con código " + codigo + " actualizado correctamente.",
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            listar();
            limpiarCampos();
        } else {
            String error = dao.getUltimoError();
            if (error == null || error.isBlank()) {
                error = "No se pudo actualizar el almacén.";
            }
            JOptionPane.showMessageDialog(vista, error, "Error al actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * DELETE: Elimina el almacén seleccionado.
     */
    private void eliminar() {
        String strCodigo = vista.txtCodigo.getText().trim();
        if (strCodigo.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione de la tabla el almacén que desea eliminar.",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = Integer.parseInt(strCodigo);
        String nombre = vista.txtNombre.getText().trim();

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de eliminar el almacén [" + codigo + " - " + nombre + "]?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (dao.eliminar(codigo)) {
                JOptionPane.showMessageDialog(vista,
                        "Almacén eliminado correctamente.",
                        "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                listar();
                limpiarCampos();
            } else {
                String error = dao.getUltimoError();
                if (error == null || error.isBlank()) {
                    error = "No se pudo eliminar el almacén.";
                }
                JOptionPane.showMessageDialog(vista, error, "Error al eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga los datos de la fila seleccionada en las cajas de texto del formulario.
     */
    private void seleccionarFila() {
        int fila = vista.tablaAlmacenes.getSelectedRow();
        if (fila >= 0) {
            Object cod = vista.tablaAlmacenes.getValueAt(fila, 0);
            Object nom = vista.tablaAlmacenes.getValueAt(fila, 1);
            Object dir = vista.tablaAlmacenes.getValueAt(fila, 2);
            Object ger = vista.tablaAlmacenes.getValueAt(fila, 3);

            vista.txtCodigo.setText(cod != null ? cod.toString() : "");
            vista.txtNombre.setText(nom != null ? nom.toString() : "");
            vista.txtDireccion.setText(dir != null ? dir.toString() : "");
            vista.txtGerente.setText(ger != null ? ger.toString() : "");
        }
    }

    /**
     * Limpia los campos del formulario para permitir el ingreso de un nuevo registro.
     */
    private void limpiarCampos() {
        vista.txtCodigo.setText("");
        vista.txtNombre.setText("");
        vista.txtDireccion.setText("");
        vista.txtGerente.setText("");
        vista.tablaAlmacenes.clearSelection();
        vista.txtNombre.requestFocus();
    }
}
