# CRUD Almacén - MercaFácil (Java Swing + PostgreSQL)

Aplicación de escritorio desarrollada en Java utilizando la arquitectura **MVC (Modelo - Vista - Controlador)** y el patrón **DAO (Data Access Object)**, conectada a la base de datos PostgreSQL (`MercaFacil`).

---

## 📌 Funcionalidades Soportadas

1. **Crear (Create):** El usuario ingresa Nombre, Dirección y Gerente. El **código es generado automáticamente** por la base de datos mediante una secuencia autoincremental (`SERIAL` / `nextval`).
2. **Leer / Listar (Read):** Muestra todos los registros existentes en una tabla interactiva con scroll.
3. **Buscar (Search):** Barra de búsqueda para filtrar registros en tiempo real por código, nombre, dirección o gerente.
4. **Actualizar (Update):** Al hacer clic en un registro de la tabla, los datos se cargan en el formulario para modificarlos y guardarlos con un clic.
5. **Eliminar (Delete):** Permite eliminar el almacén seleccionado previa confirmación, con protección contra violación de claves foráneas.

---

## 🗄️ Esquema de la Tabla y Secuencia en PostgreSQL

```sql
-- Secuencia para autoincremento
CREATE SEQUENCE IF NOT EXISTS almacen_codigo_almacen_seq OWNED BY almacen.codigo_almacen;
SELECT setval('almacen_codigo_almacen_seq', COALESCE((SELECT MAX(codigo_almacen) FROM almacen), 0));

-- Tabla con default nextval
CREATE TABLE IF NOT EXISTS public.almacen (
    codigo_almacen INTEGER NOT NULL DEFAULT nextval('almacen_codigo_almacen_seq'),
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150),
    nombre_gerente VARCHAR(100),
    CONSTRAINT almacen_pkey PRIMARY KEY (codigo_almacen)
);
```

---

## 🚀 Cómo Ejecutar la Aplicación

### Desde la Terminal / Consola:
```bash
mvn compile exec:java
```

### Para Ejecutar las Pruebas Automatizadas (6 Pruebas Unitarias):
```bash
mvn test
```

### Desde tu IDE (NetBeans, Eclipse, IntelliJ, VS Code):
- Abre la carpeta del proyecto `c:\Users\juan-\Desktop\CRUD`.
- Ejecuta la clase `com.mercafacil.crud.Main.java`.
