package com.example.viverocom.Clases;

import java.util.ArrayList;
import java.util.List;

public class Home {

    private String titulo;
    private ArrayList<Producto> listaProductos;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
