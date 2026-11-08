package com.mercafacil.crud.dao;

import com.mercafacil.crud.conexion.Conexion;
import com.mercafacil.crud.model.AlmacenModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Almacen.
 * Gestiona las operaciones de persistencia: Crear, Leer, Actualizar, Eliminar, Listar y Buscar.
 * Implementa el algoritmo de "Relleno de Huecos" (Gap-Filling) para garantizar códigos
 * estrictamente consecutivos y reutilización inteligente del primer código disponible.
 */
public class AlmacenDAO {

    private String ultimoError = "";

    public String getUltimoError() {
        return ultimoError;
    }

    /**
     * Algoritmo de detección del primer código libre disponible.
     * Si no existe el 1, retorna 1.
     * Si se eliminó un registro intermedio (ej: se borró el 11 y existen 12 y 13), retorna 11.
     * Si no hay huecos, retorna el consecutivo siguiente (MAX + 1).
     */
    public int obtenerSiguienteCodigoLibre(Connection con) throws SQLException {
        String sql = "SELECT CASE " +
                     "  WHEN NOT EXISTS (SELECT 1 FROM almacen WHERE codigo_almacen = 1) THEN 1 " +
                     "  ELSE COALESCE( " +
                     "    (SELECT t1.codigo_almacen + 1 " +
                     "     FROM almacen t1 " +
                     "     WHERE NOT EXISTS ( " +
                     "       SELECT 1 FROM almacen t2 WHERE t2.codigo_almacen = t1.codigo_almacen + 1 " +
                     "     ) " +
                     "     ORDER BY t1.codigo_almacen + 1 ASC " +
                     "     LIMIT 1), " +
                     "    1 " +
                     "  ) " +
                     "END AS siguiente_codigo";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente_codigo");
            }
        }
        return 1;
    }

    /**
     * Sincroniza la secuencia de PostgreSQL con el número máximo actual para coherencia.
     */
    private void sincronizarSecuencia(Connection con) {
        String sql = "SELECT CASE " +
                     "  WHEN MAX(codigo_almacen) IS NULL THEN setval('almacen_codigo_almacen_seq', 1, false) " +
                     "  ELSE setval('almacen_codigo_almacen_seq', MAX(codigo_almacen), true) " +
                     "END FROM almacen";
        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            // No detiene el flujo si la secuencia no existe
            System.err.println("Aviso al sincronizar secuencia: " + e.getMessage());
        }
    }

    /**
     * 1. CREATE: Inserta un nuevo almacén aplicando el algoritmo de relleno de huecos.
     */
    public boolean insertar(AlmacenModel almacen) {
        ultimoError = "";
        String sql = "INSERT INTO almacen (codigo_almacen, nombre, direccion, nombre_gerente) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                ultimoError = "No se pudo conectar a la base de datos.";
                return false;
            }

            // 1. Obtener el primer código disponible según el algoritmo
            int nuevoCodigo = obtenerSiguienteCodigoLibre(con);

            // 2. Insertar con el código asignado automáticamente
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nuevoCodigo);
                ps.setString(2, almacen.getNombre());
                ps.setString(3, almacen.getDireccion());
                ps.setString(4, almacen.getNombre_gerente());

                int filas = ps.executeUpdate();
                if (filas > 0) {
                    almacen.setCodigo_almacen(nuevoCodigo);
                    sincronizarSecuencia(con);
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error al insertar almacén: " + e.getMessage());
            return false;
        }
    }

    /**
     * 2. READ / LISTAR: Obtiene todos los almacenes registrados ordenados por código.
     */
    public List<AlmacenModel> listar() {
        ultimoError = "";
        List<AlmacenModel> lista = new ArrayList<>();
        String sql = "SELECT codigo_almacen, nombre, direccion, nombre_gerente FROM almacen ORDER BY codigo_almacen ASC";
        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                ultimoError = "No se pudo conectar a la base de datos.";
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AlmacenModel(
                        rs.getInt("codigo_almacen"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("nombre_gerente")
                    ));
                }
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error al listar almacenes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * 3. BUSCAR: Filtra almacenes por código, nombre, dirección o gerente.
     */
    public List<AlmacenModel> buscar(String criterio) {
        ultimoError = "";
        List<AlmacenModel> lista = new ArrayList<>();
        String sql = "SELECT codigo_almacen, nombre, direccion, nombre_gerente FROM almacen " +
                     "WHERE CAST(codigo_almacen AS TEXT) LIKE ? " +
                     "   OR LOWER(nombre) LIKE LOWER(?) " +
                     "   OR LOWER(direccion) LIKE LOWER(?) " +
                     "   OR LOWER(nombre_gerente) LIKE LOWER(?) " +
                     "ORDER BY codigo_almacen ASC";

        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                ultimoError = "No se pudo conectar a la base de datos.";
                return lista;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String patron = "%" + criterio.trim() + "%";
                ps.setString(1, patron);
                ps.setString(2, patron);
                ps.setString(3, patron);
                ps.setString(4, patron);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new AlmacenModel(
                            rs.getInt("codigo_almacen"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getString("nombre_gerente")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error al buscar almacenes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca un único almacén por su clave primaria exacta.
     */
    public AlmacenModel buscarPorCodigo(int codigo_almacen) {
        ultimoError = "";
        String sql = "SELECT codigo_almacen, nombre, direccion, nombre_gerente FROM almacen WHERE codigo_almacen = ?";
        try (Connection con = Conexion.conectar()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codigo_almacen);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AlmacenModel(
                            rs.getInt("codigo_almacen"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getString("nombre_gerente")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error al buscar por código: " + e.getMessage());
        }
        return null;
    }

    /**
     * 4. UPDATE: Actualiza los datos de un almacén existente.
     */
    public boolean actualizar(AlmacenModel almacen) {
        ultimoError = "";
        String sql = "UPDATE almacen SET nombre = ?, direccion = ?, nombre_gerente = ? WHERE codigo_almacen = ?";
        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                ultimoError = "No se pudo conectar a la base de datos.";
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, almacen.getNombre());
                ps.setString(2, almacen.getDireccion());
                ps.setString(3, almacen.getNombre_gerente());
                ps.setInt(4, almacen.getCodigo_almacen());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.err.println("Error al actualizar almacén: " + e.getMessage());
            return false;
        }
    }

    /**
     * 5. DELETE: Elimina un almacén por su código primario.
     */
    public boolean eliminar(int codigo_almacen) {
        ultimoError = "";
        String sql = "DELETE FROM almacen WHERE codigo_almacen = ?";
        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                ultimoError = "No se pudo conectar a la base de datos.";
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codigo_almacen);
                boolean eliminado = ps.executeUpdate() > 0;
                if (eliminado) {
                    sincronizarSecuencia(con);
                }
                return eliminado;
            }
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                ultimoError = "No se puede eliminar el almacén " + codigo_almacen + 
                              " porque tiene ventas, compras o artículos asociados.";
            } else {
                ultimoError = e.getMessage();
            }
            System.err.println("Error al eliminar almacén: " + e.getMessage());
            return false;
        }
    }
}
