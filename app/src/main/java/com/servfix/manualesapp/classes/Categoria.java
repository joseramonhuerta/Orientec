package com.servfix.manualesapp.classes;

public class Categoria {
    int id_categoria;
    String nombre_categoria;
    String icono;
    String imagen_categoria;

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre_categoria() {
        return nombre_categoria;
    }

    public void setNombre_categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getImagen_categoria() {
        return imagen_categoria;
    }

    public void setImagen_categoria(String imagen_categoria) {
        this.imagen_categoria = imagen_categoria;
    }
}
