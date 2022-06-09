package com.servfix.manualesapp;

import java.io.Serializable;
import java.util.Date;

public class MensajeChat implements Serializable {
    public String id_usuario_manual, id_usuario_envia, id_usuario_recibe, mensaje, fecha_mensaje_formateada, imagen,id_usuario_firebase_envia, id_usuario_firebase_recibe, nombre_usuario_envia, nombre_usuario_recibe;
    public Date fecha_mensaje;
    public String id_conversacion, nombre_usuario_conversacion, imagen_conversacion, nombre_manual_conversacion, fecha_conversacion,ultimo_mensaje, status;
    public int puede_calificar;
}
