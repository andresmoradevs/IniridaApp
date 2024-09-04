package com.afmv.iniridaapp.models;

import java.io.Serializable;
import java.util.Map;

public class StoreItem implements Serializable {

    private String nombre;
    private String categoria;
    private String contacto;
    private String descripcion;
    private String id;
    private Map<String, String> imagenes;

    public StoreItem() {
    }

    public StoreItem(String nombre, String categoria, String contacto, String descripcion, String id, Map<String, String> imagenes) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.id = id;
        this.imagenes = imagenes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Map<String, String> imagenes) {
        this.imagenes = imagenes;
    }
}
