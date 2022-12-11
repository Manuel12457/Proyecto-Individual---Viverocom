package com.example.viverocom.Clases;

import java.util.Map;

public class Consulta {

    private String id;
    private String idUsuario;
    private String asunto;
    private String cuerpo;
    private String fechahoraconsulta;
    private String idrespuesta;
    private Map<String, String> imagenes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getIdrespuesta() {
        return idrespuesta;
    }

    public void setIdrespuesta(String idrespuesta) {
        this.idrespuesta = idrespuesta;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getFechahoraconsulta() {
        return fechahoraconsulta;
    }

    public void setFechahoraconsulta(String fechahoraconsulta) {
        this.fechahoraconsulta = fechahoraconsulta;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Map<String, String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Map<String, String> imagenes) {
        this.imagenes = imagenes;
    }
}
