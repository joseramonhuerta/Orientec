package com.servfix.manualesapp;

import java.io.Serializable;

public class ClaseChat implements Serializable {
    public int id_usuario_manual;
    public int id_usuario_sender;
    public int id_usuario_receiver;
    public String nombre_usuario_sender;
    public String nombre_usuario_receiver;
    public String nombre_manual;
    public String imagen;
    public String imagen_sender;
    public String id_usuario_firebase;
    public String id_usuario_firebase_sender;
    public String token;
    /*
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

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getNombre_manual() {
        return nombre_manual;
    }

    public void setNombre_manual(String nombre_manual) {
        this.nombre_manual = nombre_manual;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId_usuario_firebase() {
        return id_usuario_firebase;
    }

    public void setId_usuario_firebase(String id_usuario_firebase) {
        this.id_usuario_firebase = id_usuario_firebase;
    }
    */

}
