package com.servfix.manualesapp.utilities;

import com.servfix.manualesapp.classes.User;

import java.util.HashMap;

public class Constants {
    //Users
    public static final String KEY_USERS = "users";
    public static final String KEY_LOGEADO = "logeado";
    public static final String KEY_USUARIO = "usuario";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL_USUARIO = "email_usuario";
    public static final String KEY_NOMBRE_USUARIO = "nombre_usuario";
    public static final String KEY_ID_USUARIO = "id_usuario";
    public static final String KEY_PATERNO_USUARIO = "paterno_usuario";
    public static final String KEY_MATERNO_USUARIO = "materno_usuario";
    public static final String KEY_CELULAR = "celular";
    public static final String KEY_TIPO_USUARIO = "tipo_usuario";
    public static final String KEY_ID_USUARIO_FIREBASE = "id_usuario_firebase";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_CONOCIMIENTOS_TECNICOS = "conocimientos_tecnicos";
    public static final String KEY_BENEFICIARIO = "beneficiario";
    public static final String KEY_CUENTA_BANCARIA = "cuenta_bancaria";
    //Chats
    public static final String KEY_CHATS= "chats";
    public static final String KEY_ID_USUARIO_ENVIA = "id_usuario_envia";
    public static final String KEY_ID_USUARIO_FIREBASE_ENVIA = "id_usuario_firebase_envia";
    public static final String KEY_NOMBRE_USUARIO_ENVIA = "nombre_usuario_envia";
    public static final String KEY_MENSAJE = "mensaje";
    //Notifications
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_TOPICS = "to";
    public static final String KEY_ENLINIA = "enlinea";
    //Conversations
    public static final String KEY_CONVERSATIONS = "conversations";
    public static final String KEY_ID_USUARIO_MANUAL = "id_usuario_manual";
    public static final String KEY_NOMBRE_MANUAL = "nombre_manual";
    public static final String KEY_ID_USUARIO_RECIBE = "id_usuario_recibe";
    public static final String KEY_NOMBRE_USUARIO_RECIBE = "nombre_usuario_recibe";
    public static final String KEY_IMAGEN_RECIBE = "imagen_recibe";
    public static final String KEY_ID_USUARIO_FIREBASE_RECIBE = "id_usuario_firebase_recibe";
    public static final String KEY_ULTIMO_MENSAJE = "ultimo_mensaje";
    public static final String KEY_FECHA_MENSAJE = "fecha_mensaje";
    public static final String KEY_CONVERSATIONS_STATUS = "status";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "KEY=AAAAwaLQcwg:APA91bHSjBxeMszub-V_F3F8RYKKVfs07jgCDso7ox7rUKXy6m0ExkW-_mSXcJpsPavHEHJdQzMp2fRAc7zS8a2nKQHNqwanSShuB3nKVCC6JyaW-cA_yDqsxhi6isIbIsZlBVbdNCVb"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
