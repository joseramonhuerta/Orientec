package com.servfix.manualesapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.servfix.manualesapp.activities.BaseActivity;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.interfaces.ApiService;
import com.servfix.manualesapp.network.ApiClient;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSoporte extends BaseActivity {
    int id_usuario=0;
    private ClaseChat manual;
    private ActivityChatSoporteBinding binding;
    private List<MensajeChat> chatMessages;
    private ChatAdapter chatAdapter;
    private GlobalVariables gv;
    private FirebaseFirestore database;
    private String receiverToken ="";
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_soporte);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityChatSoporteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gv = new GlobalVariables();
        id_usuario = gv.id_usuario;
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, getBitmapFromEncodedString(manual.imagen), String.valueOf(id_usuario));
        binding.listviewChat.setAdapter(chatAdapter);
        binding.listviewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        database = FirebaseFirestore.getInstance();
        //getTokerReceiver(manual.id_usuario_firebase);
    }

    private void sendMessage(){
        GlobalVariables gv= new GlobalVariables();
        HashMap<String, Object> message = new HashMap<>();
        message.put("id_usuario_manual", String.valueOf(manual.id_usuario_manual));
        message.put("id_usuario_envia", String.valueOf(id_usuario));
        message.put("id_usuario_recibe", String.valueOf(manual.id_usuario_receiver));
        message.put("mensaje", binding.txtSendMessage.getText().toString());
        message.put("fecha_mensaje", new Date());
        database.collection("chats").add(message);
        if(!isReceiverAvailable){
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(receiverToken);

                JSONObject data = new JSONObject();
                data.put("id_usuario", manual.id_usuario_sender);
                data.put("id_usuario_manual", String.valueOf(manual.id_usuario_manual));
                data.put("nombre_usuario_sender", manual.nombre_usuario_sender);
                data.put("nombre_usuario_receiver", manual.nombre_usuario_receiver);
                data.put("nombre_manual", manual.nombre_manual);
                data.put("id_usuario_firebase", manual.id_usuario_firebase_sender);
                data.put("id_usuario_firebase_sender", manual.id_usuario_firebase);
                /*data.put("imagen", manual.imagen_sender);*/
                //data.put("fcmToken", gv.fcmToken);
                data.put("mensaje", binding.txtSendMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put("data", data);
                body.put("registration_ids", tokens);

                sendNotification(body.toString());
            }catch (Exception e){
                showToast(e.getMessage());
            }

        }

        binding.txtSendMessage.setText(null);

    }

    private void getTokerReceiver(String receiverId){

        database.collection("users").document(receiverId.toString()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            GlobalVariables vg = new GlobalVariables();
                            String token = documentSnapshot.getString("fcmToken");
                            vg.fcmToken = token;
                            receiverToken = token;
                        }
                    }
                });
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }else{
            return null;
        }
    }

    private void loadReceiverDetails(){
        manual = (ClaseChat) getIntent().getExtras().getSerializable("manual");
        binding.txtTituloCursoChat.setText(manual.nombre_manual);
        binding.txtUsuarioChat.setText(manual.nombre_usuario_receiver);
        binding.ivProfileChat.setImageBitmap(getBitmapFromEncodedString(manual.imagen));
    }

    private void setListeners(){
        binding.btnAtrasChat.setOnClickListener(v -> onBackPressed());
        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarMensaje())
                    return;

                sendMessage();

            }
        });
    }

    private Boolean validarMensaje(){
        String val = binding.txtSendMessage.getText().toString();

        if(val.isEmpty()){
            return false;
        }else{
            return true;
        }

    }

    private void listenAvailabilityReceiver(){
        database.collection("users").document(
                manual.id_usuario_firebase
        ).addSnapshotListener(ChatSoporte.this, (value, error) ->{
           if(error != null){
                return;
           }
           if(value != null){
               if(value.getLong(Constants.KEY_ENLINIA) != null){
                   int availability = Objects.requireNonNull(
                           value.getLong(Constants.KEY_ENLINIA)
                   ).intValue();
                   isReceiverAvailable = availability == 1;
               }
               receiverToken = value.getString(Constants.KEY_FCM_TOKEN);
               if(manual.imagen == null){
                   manual.imagen_sender = value.getString("imagen");
                   chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(manual.imagen_sender));
                   binding.ivProfileChat.setImageBitmap(getBitmapFromEncodedString(manual.imagen_sender));
                   chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
               }
           }
           if(isReceiverAvailable){
               binding.txtStatusChat.setText("En linea");
           }else{
               binding.txtStatusChat.setText(null);
           }
        });
    }

    private void listenMessages(){
        database.collection("chats")
                .whereEqualTo("id_usuario_manual", String.valueOf(manual.id_usuario_manual))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
      if(error != null){
          return;
      }
      if(value != null){
           int count = chatMessages.size();
           for(DocumentChange documentChange : value.getDocumentChanges()){
               if(documentChange.getType() == DocumentChange.Type.ADDED){
                   MensajeChat mensajeChat = new MensajeChat();
                   mensajeChat.id_usuario_manual = documentChange.getDocument().getString("id_usuario_manual");
                   mensajeChat.id_usuario_envia = documentChange.getDocument().getString("id_usuario_envia");
                   mensajeChat.id_usuario_recibe = documentChange.getDocument().getString("id_usuario_recibe");
                   mensajeChat.mensaje = documentChange.getDocument().getString("mensaje");
                   mensajeChat.fecha_mensaje = documentChange.getDocument().getDate("fecha_mensaje");
                   mensajeChat.fecha_mensaje_formateada = getReadableDateTime(documentChange.getDocument().getDate("fecha_mensaje"));
                  chatMessages.add(mensajeChat);
               }
           }
          Collections.sort(chatMessages, (obj1, obj2)-> obj1.fecha_mensaje.compareTo(obj2.fecha_mensaje));
           if(count == 0){
               chatAdapter.notifyDataSetChanged();
           }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.listviewChat.smoothScrollToPosition(chatMessages.size() - 1);
           }
           binding.progressBarChat.setVisibility(View.GONE);
           if(chatMessages.size() > 0)
               binding.listviewChat.smoothScrollToPosition(chatMessages.size() - 1);
      }
    };

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if(response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }else{
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityReceiver();
    }
}