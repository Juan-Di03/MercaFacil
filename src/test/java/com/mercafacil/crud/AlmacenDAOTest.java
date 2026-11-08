package com.mercafacil.crud;

import com.mercafacil.crud.conexion.Conexion;
import com.mercafacil.crud.dao.AlmacenDAO;
import com.mercafacil.crud.model.AlmacenModel;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlmacenDAOTest {

    private static AlmacenDAO dao;

    @BeforeAll
    static void setUp() {
        dao = new AlmacenDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Debe conectar exitosamente a la base de datos MercaFacil")
    void testConexion() {
        assertTrue(Conexion.probarConexion(), "La conexión con PostgreSQL debe ser exitosa");
    }

    @Test
    @Order(2)
    @DisplayName("Debe listar los almacenes existentes")
    void testListar() {
        List<AlmacenModel> lista = dao.listar();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Debe rellenar huecos automáticamente al insertar tras una eliminación")
    void testAlgoritmoRellenoHuecos() {
        // 1. Insertar dos registros consecutivos
        AlmacenModel reg1 = new AlmacenModel("Test Hueco 1", "Calle 1", "Gerente 1");
        assertTrue(dao.insertar(reg1));
        int id1 = reg1.getCodigo_almacen();

        AlmacenModel reg2 = new AlmacenModel("Test Hueco 2", "Calle 2", "Gerente 2");
        assertTrue(dao.insertar(reg2));
        int id2 = reg2.getCodigo_almacen();

        assertTrue(id2 > id1, "El segundo registro debe tener un ID mayor al primero");

        // 2. Eliminar el primer registro (creando un hueco en id1)
        assertTrue(dao.eliminar(id1));

        // 3. Insertar un tercer registro: el algoritmo DEBE detectar el hueco y reasignar id1
        AlmacenModel reg3 = new AlmacenModel("Test Relleno Hueco", "Calle 3", "Gerente 3");
        assertTrue(dao.insertar(reg3));
        assertEquals(id1, reg3.getCodigo_almacen(), "El algoritmo debe reasignar exactamente el código del hueco libre");

        // Limpieza de registros de prueba
        dao.eliminar(id1);
        dao.eliminar(id2);
    }

    @Test
    @Order(4)
    @DisplayName("Debe buscar almacenes por criterio")
    void testBuscar() {
        List<AlmacenModel> encontrados = dao.buscar("Centro");
        assertNotNull(encontrados);
        assertFalse(encontrados.isEmpty());
    }
}
