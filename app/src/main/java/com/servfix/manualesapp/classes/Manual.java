package com.servfix.manualesapp.classes;

import java.io.Serializable;

public class Manual implements Serializable {
    int id_manual;
    int id_usuario_manual;
    int id_usuario;
    int id_usuario_tecnico;
    int id_categoria;
    String nombre_manual;
    String descripcion_manual;
    String paginas;
    String portada;
    String imagen_detalle;
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
    String nombre_categoria;
    int esgratuito;
    int obtenido;
    double calificacion;
    String url_portada;
    String url_detalle;
    int status_manual;
    int esnuevo;
    int puede_calificar;
    String fecha_compra;

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

    public int getEsgratuito() {
        return esgratuito;
    }

    public void setEsgratuito(int esgratuito) {
        this.esgratuito = esgratuito;
    }

    public int getObtenido() {
        return obtenido;
    }

    public void setObtenido(int obtenido) {
        this.obtenido = obtenido;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getImagen_detalle(){
        return imagen_detalle;
    }

    public void setImagen_detalle(String imagen_detalle){
        this.imagen_detalle = imagen_detalle;
    }

    public String getUrl_portada() {
        return url_portada;
    }

    public void setUrl_portada(String url_portada) {
        this.url_portada = url_portada;
    }

    public String getUrl_detalle() {
        return url_detalle;
    }

    public void setUrl_detalle(String url_detalle) {
        this.url_detalle = url_detalle;
    }

    public void setStatus_manual(int status_manual){
        this.status_manual = status_manual;
    }

    public int getStatus_manual(){
        return status_manual;
    }

    public int getEsnuevo() {
        return esnuevo;
    }

    public void setEsnuevo(int esnuevo) {
        this.esnuevo = esnuevo;
    }

    public int getPuede_calificar() {
        return puede_calificar;
    }

    public void setPuede_calificar(int puede_calificar) {
        this.puede_calificar = puede_calificar;
    }

    public String getFecha_compra(){
        return fecha_compra;
    }

    public void setFecha_compra(String fecha_compra){
        this.fecha_compra = fecha_compra;
    }

}
