package com.mercafacil.crud.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Interfaz gráfica de usuario (GUI) en Swing para el CRUD de Almacén.
 * Incluye campos autoincrementales, operaciones CRUD y barra de búsqueda.
 */
public class AlmacenView extends JFrame {

    // Componentes del formulario
    public JTextField txtCodigo;
    public JTextField txtNombre;
    public JTextField txtDireccion;
    public JTextField txtGerente;

    // Botones de acción CRUD
    public JButton btnGuardar;
    public JButton btnActualizar;
    public JButton btnEliminar;
    public JButton btnLimpiar;

    // Componentes de búsqueda
    public JTextField txtBuscar;
    public JButton btnBuscar;
    public JButton btnRefrescar;

    // Tabla de visualización de datos
    public JTable tablaAlmacenes;
    public DefaultTableModel tableModel;

    public AlmacenView() {
        super("MercaFácil - Gestión de Almacenes");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        // Panel principal con BorderLayout y márgenes
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(panelPrincipal);

        // --- ENCABEZADO ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Módulo de Gestión de Almacenes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 42, 86));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 8, 0));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // --- PANEL CENTRAL: FORMULARIO Y BOTONES CRUD ---
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Datos del Almacén ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(47, 53, 66)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Código (Autoincremental) y Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        panelForm.add(new JLabel("Código:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35;
        txtCodigo = new JTextField(10);
        txtCodigo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtCodigo.setEditable(false);
        txtCodigo.setFocusable(false);
        txtCodigo.setBackground(new Color(238, 240, 243));
        txtCodigo.setToolTipText("El código es generado automáticamente por la base de datos.");
        panelForm.add(txtCodigo, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        panelForm.add(new JLabel("Nombre (*):"), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.45;
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelForm.add(txtNombre, gbc);

        // Fila 1: Dirección y Gerente
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        panelForm.add(new JLabel("Dirección:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.35;
        txtDireccion = new JTextField(15);
        txtDireccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelForm.add(txtDireccion, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        panelForm.add(new JLabel("Gerente:"), gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.45;
        txtGerente = new JTextField(20);
        txtGerente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelForm.add(txtGerente, gbc);

        panelCentro.add(panelForm, BorderLayout.CENTER);

        // Botones de acción CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Nuevo / Limpiar");

        Font fontBotones = new Font("Segoe UI", Font.BOLD, 12);
        btnGuardar.setFont(fontBotones);
        btnActualizar.setFont(fontBotones);
        btnEliminar.setFont(fontBotones);
        btnLimpiar.setFont(fontBotones);

        btnGuardar.setToolTipText("Registrar nuevo almacén (código automático)");
        btnActualizar.setToolTipText("Actualizar el almacén seleccionado");
        btnEliminar.setToolTipText("Eliminar el almacén seleccionado");
        btnLimpiar.setToolTipText("Limpiar campos para un nuevo registro");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelCentro.add(panelBotones, BorderLayout.SOUTH);
        panelPrincipal.add(panelCentro, BorderLayout.NORTH);

        // --- PANEL INFERIOR: BÚSQUEDA Y TABLA DE DATOS ---
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(8, 8));
        panelTablaContenedor.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Almacenes Registrados ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(47, 53, 66)));

        // Barra de Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por código, nombre, dirección o gerente...");

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar = new JButton("Ver Todos");
        btnRefrescar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnRefrescar);

        panelTablaContenedor.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla con columnas
        String[] columnas = {"Código", "Nombre", "Dirección", "Gerente"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura en celdas
            }
        };

        tablaAlmacenes = new JTable(tableModel);
        tablaAlmacenes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaAlmacenes.setRowHeight(24);
        tablaAlmacenes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaAlmacenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tablaAlmacenes);
        scrollTabla.setPreferredSize(new Dimension(860, 260));
        panelTablaContenedor.add(scrollTabla, BorderLayout.CENTER);

        panelPrincipal.add(panelTablaContenedor, BorderLayout.CENTER);
    }
}
