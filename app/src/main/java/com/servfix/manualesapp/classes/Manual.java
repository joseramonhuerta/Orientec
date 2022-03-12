package com.servfix.manualesapp.classes;

import java.io.Serializable;

public class Manual implements Serializable {
    int id_manual;
    int id_usuario_manual;
    int id_usuario;
    int id_usuario_tecnico;
    String nombre_manual;
    String descripcion_manual;
    String paginas;
    String portada;
    String nombre_pdf;
    String url;
    double precio;
    int tipo;
    String tipo_descripcion;
    String nombre_tecnico;
    String imagen_tecnico;
    String nombre_usuario;
    String imagen_usuario;
    String id_usuario_firebase;
    String id_usuario_firebase_sender;

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getTipo_descripcion() {
        return tipo_descripcion;
    }

    public void setTipo_descripcion(String tipo_descripcion) {
        this.tipo_descripcion = tipo_descripcion;
    }

    public String getNombre_pdf() {
        return nombre_pdf;
    }

    public void setNombre_pdf(String nombre_pdf) {
        this.nombre_pdf = nombre_pdf;
    }

    public int getId_manual() {
        return id_manual;
    }

    public void setId_manual(int id_manual) {
        this.id_manual = id_manual;
    }

    public String getNombre_manual() {
        return nombre_manual;
    }

    public void setNombre_manual(String nombre_manual) {
        this.nombre_manual = nombre_manual;
    }

    public String getDescripcion_manual() {
        return descripcion_manual;
    }

    public void setDescripcion_manual(String descripcion_manual) {
        this.descripcion_manual = descripcion_manual;
    }

    public String getPaginas() {
        return paginas;
    }

    public void setPaginas(String paginas) {
        this.paginas = paginas;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId_usuario_manual() {
        return id_usuario_manual;
    }

    public void setId_usuario_manual(int id_usuario_manual) {
        this.id_usuario_manual = id_usuario_manual;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_usuario_tecnico() {
        return id_usuario_tecnico;
    }

    public void setId_usuario_tecnico(int id_usuario_tecnico) {
        this.id_usuario_tecnico = id_usuario_tecnico;
    }

    public void setNombre_tecnico(String nombre){
        this.nombre_tecnico = nombre;
    }

    public String getNombre_tecnico(){return this.nombre_tecnico;}

    public String getImagen_tecnico() {
        return imagen_tecnico;
    }

    public void setImagen_tecnico(String imagen_tecnico) {
        this.imagen_tecnico = imagen_tecnico;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getImagen_usuario() {
        return imagen_usuario;
    }

    public void setImagen_usuario(String imagen_usuario) {
        this.imagen_usuario = imagen_usuario;
    }

    public String getId_usuario_firebase() {
        return id_usuario_firebase;
    }

    public void setId_usuario_firebase(String id_usuario_firebase) {
        this.id_usuario_firebase = id_usuario_firebase;
    }

    public String getId_usuario_firebase_sender() {
        return id_usuario_firebase_sender;
    }

    public void setId_usuario_firebase_sender(String id_usuario_firebase_sender) {
        this.id_usuario_firebase_sender = id_usuario_firebase_sender;
    }
}