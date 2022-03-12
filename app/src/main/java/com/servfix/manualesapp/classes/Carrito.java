package com.servfix.manualesapp.classes;

public class Carrito {
    int id_carrito_compras_detalle;
    int id_carrito_compras;
    int id_manual;
    String nombre_manual;
    double precio;
    String portada;

    public int getId_carrito_compras_detalle() {
        return id_carrito_compras_detalle;
    }

    public void setId_carrito_compras_detalle(int id_carrito_compras_detalle) {
        this.id_carrito_compras_detalle = id_carrito_compras_detalle;
    }

    public int getId_carrito_compras() {
        return id_carrito_compras;
    }

    public void setId_carrito_compras(int id_carrito_compras) {
        this.id_carrito_compras = id_carrito_compras;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }
}
