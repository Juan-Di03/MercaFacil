package com.mercafacil.crud.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de conexión a la base de datos PostgreSQL MercaFacil.
 */
public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/MercaFacil";
    private static final String USER = "postgres";
    private static final String PASSWORD = "***********";

    /**
     * Obtiene una nueva conexión a PostgreSQL.
     * @return Connection si la conexión fue exitosa, o null en caso de error.
     */
    public static Connection conectar() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return con;
    }

    /**
     * Verifica si es posible establecer comunicación con la base de datos.
     * @return true si la conexión es válida y activa.
     */
    public static boolean probarConexion() {
        try (Connection c = conectar()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
