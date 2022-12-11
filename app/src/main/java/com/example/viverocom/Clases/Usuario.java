package com.example.viverocom.Clases;

import java.util.Map;

public class Usuario {

    private String id;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccion;
    private String rol;
    private Map<String, String> imagen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Map<String, String> getImagen() {
        return imagen;
    }

    public void setImagen(Map<String, String> imagen) {
        this.imagen = imagen;
    }
}
