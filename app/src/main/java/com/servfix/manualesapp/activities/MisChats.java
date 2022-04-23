package com.servfix.manualesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.servfix.manualesapp.ChatSoporte;
import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.MensajeChat;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.adapters.ConversacionesAdapter;
import com.servfix.manualesapp.classes.User;
import com.servfix.manualesapp.databinding.ActivityMisChatsBinding;
import com.servfix.manualesapp.databinding.ActivityMisCursosTecnicoBinding;
import com.servfix.manualesapp.listeners.ConversacionesListerner;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.PreferenceManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MisChats extends AppCompatActivity implements ConversacionesListerner {
    private User receiverUser;
    private List<MensajeChat> conversations;
    private ConversacionesAdapter conversationsAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversacionId = null;
    ActivityMisChatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityMisChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(MisChats.this);
        init();
        //getToken();
        setListeners();
        listenConversations();


    }

    private void setListeners(){
        binding.btnAtrasMisChats.setOnClickListener(v -> onBackPressed());
    }

    private void init(){
        conversations = new ArrayList<>();
        conversationsAdapter = new ConversacionesAdapter(conversations, this);
        binding.listviewConversacion.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void showToast(String message){
        Toast.makeText(MisChats.this, message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations(){
        database.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_ID_USUARIO_RECIBE, String.valueOf(preferenceManager.getInt(Constants.KEY_ID_USUARIO)))
                .addSnapshotListener(eventListener);
        /*database.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_ID_USUARIO_RECIBE, preferenceManager.getInt(Constants.KEY_ID_USUARIO_RECIBE))
                .addSnapshotListener(eventListener);*/
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        binding.progressBarChatConversations.setVisibility(View.GONE);
        if(value != null){
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_ENVIA);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_RECIBE);
                    MensajeChat mensajeChat = new MensajeChat();
                    mensajeChat.id_usuario_envia = senderId;
                    mensajeChat.id_usuario_recibe = receiverId;
                    if (String.valueOf(preferenceManager.getInt(Constants.KEY_ID_USUARIO_ENVIA)).equals(senderId)) {
                        mensajeChat.imagen_conversacion = documentChange.getDocument().getString(Constants.KEY_IMAGEN_RECIBE);
                        mensajeChat.nombre_usuario_envia = documentChange.getDocument().getString(Constants.KEY_NOMBRE_USUARIO_ENVIA);
                        mensajeChat.nombre_usuario_recibe = documentChange.getDocument().getString(Constants.KEY_NOMBRE_USUARIO_RECIBE);
                        mensajeChat.id_conversacion = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_RECIBE);
                        mensajeChat.id_usuario_firebase_envia = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_FIREBASE_RECIBE);
                        mensajeChat.id_usuario_firebase_recibe = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_FIREBASE);

                    } else {
                        mensajeChat.imagen_conversacion = documentChange.getDocument().getString(Constants.KEY_IMAGEN);
                        mensajeChat.nombre_usuario_envia = documentChange.getDocument().getString(Constants.KEY_NOMBRE_USUARIO_RECIBE);
                        mensajeChat.nombre_usuario_recibe = documentChange.getDocument().getString(Constants.KEY_NOMBRE_USUARIO_ENVIA);
                        mensajeChat.id_conversacion = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_ENVIA);
                        mensajeChat.id_usuario_firebase_envia = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_FIREBASE_RECIBE);
                        mensajeChat.id_usuario_firebase_recibe = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_FIREBASE);
                    }
                    mensajeChat.id_usuario_manual = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_MANUAL);
                    mensajeChat.nombre_manual_conversacion = documentChange.getDocument().getString(Constants.KEY_NOMBRE_MANUAL);
                    mensajeChat.ultimo_mensaje = documentChange.getDocument().getString(Constants.KEY_ULTIMO_MENSAJE);
                    mensajeChat.fecha_conversacion = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_FECHA_MENSAJE));
                    conversations.add(mensajeChat);


                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for(int i=0; i < conversations.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_ENVIA);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_ID_USUARIO_RECIBE);
                        if(conversations.get(i).id_usuario_envia.equals(senderId) && conversations.get(i).id_usuario_recibe.equals(receiverId)){
                            conversations.get(i).ultimo_mensaje = documentChange.getDocument().getString(Constants.KEY_ULTIMO_MENSAJE);
                            conversations.get(i).fecha_conversacion = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_FECHA_MENSAJE));
                            break;
                        }
                    }
                }
            }

            if(conversations.size() > 0){
                binding.txtSinChats.setVisibility(View.GONE);
            }else{
                binding.txtSinChats.setVisibility(View.VISIBLE);
            }

            Collections.sort(conversations, (obj1, obj2) -> obj2.fecha_conversacion.compareTo(obj1.fecha_conversacion));
            conversationsAdapter.notifyDataSetChanged();
            binding.listviewConversacion.smoothScrollToPosition(0);
        }
    };

    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(@NonNull InstanceIdResult instanceIdResult) {
                String updatedToken = instanceIdResult.getToken();
                updateToken(updatedToken);
            }
        });
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_USERS).document(
                preferenceManager.getString(Constants.KEY_ID_USUARIO_FIREBASE)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("No se pudo obtener el token"));
    }

    private String getReadableDateTime(Date date){
        String fechaFormateada = new SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.getDefault()).format(date);
        return fechaFormateada;
    }


    @Override
    public void onConversionClicked(ClaseChat claseChat) {
        Intent intent = new Intent(MisChats.this, ChatSoporte.class);
        intent.putExtra("manual", (Serializable) claseChat);
        startActivity(intent);
    }
}