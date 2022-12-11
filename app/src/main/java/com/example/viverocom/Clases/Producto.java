package com.example.viverocom.Clases;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Producto {

    private String id;
    private String nombre;
    private String tipo;
    private double costo;
    private int stock;
    private String descripcion;
    private String cuidadosPlanta;
    private double tempmin;
    private double tempmax;
    private String fechaCreacion;
    private boolean estado;
    private Map<String, String> imagenes;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCuidadosPlanta() {
        return cuidadosPlanta;
    }

    public void setCuidadosPlanta(String cuidadosPlanta) {
        this.cuidadosPlanta = cuidadosPlanta;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public double getTempmin() {
        return tempmin;
    }

    public void setTempmin(double tempmin) {
        this.tempmin = tempmin;
    }

    public double getTempmax() {
        return tempmax;
    }

    public void setTempmax(double tempmax) {
        this.tempmax = tempmax;
    }

    public Map<String, String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Map<String, String> imagenes) {
        this.imagenes = imagenes;
    }
}
