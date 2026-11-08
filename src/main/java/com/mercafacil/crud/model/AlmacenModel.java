package com.mercafacil.crud.model;

import java.util.Objects;

/**
 * Modelo que representa la entidad 'almacen' de la base de datos MercaFacil.
 * El campo codigo_almacen es autoincremental en la base de datos.
 */
public class AlmacenModel {
    private int codigo_almacen;
    private String nombre;
    private String direccion;
    private String nombre_gerente;

    // Constructor vacío
    public AlmacenModel() {}

    // Constructor para nuevos registros (el código lo genera la BD automáticamente)
    public AlmacenModel(String nombre, String direccion, String nombre_gerente) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.nombre_gerente = nombre_gerente;
    }

    // Constructor completo para registros existentes
    public AlmacenModel(int codigo_almacen, String nombre, String direccion, String nombre_gerente) {
        this.codigo_almacen = codigo_almacen;
        this.nombre = nombre;
        this.direccion = direccion;
        this.nombre_gerente = nombre_gerente;
    }

    // Getters y Setters
    public int getCodigo_almacen() { 
        return codigo_almacen; 
    }
    public void setCodigo_almacen(int codigo_almacen) { 
        this.codigo_almacen = codigo_almacen; 
    }
    public int getCodigoAlmacen() { 
        return codigo_almacen; 
    }
    public void setCodigoAlmacen(int codigoAlmacen) { 
        this.codigo_almacen = codigoAlmacen; 
    }

    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getDireccion() { 
        return direccion; 
    }
    public void setDireccion(String direccion) { 
        this.direccion = direccion; 
    }

    public String getNombre_gerente() { 
        return nombre_gerente; 
    }
    public void setNombre_gerente(String nombre_gerente) { 
        this.nombre_gerente = nombre_gerente; 
    }
    public String getNombreGerente() { 
        return nombre_gerente; 
    }
    public void setNombreGerente(String nombreGerente) { 
        this.nombre_gerente = nombreGerente; 
    }

    @Override
    public String toString() {
        return "AlmacenModel{" +
                "codigo=" + codigo_almacen +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", gerente='" + nombre_gerente + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlmacenModel that = (AlmacenModel) o;
        return codigo_almacen == that.codigo_almacen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo_almacen);
    }
}
