package com.servfix.manualesapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.servfix.manualesapp.activities.BaseActivity;
import com.servfix.manualesapp.activities.CarritoCompras;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.fragments.CuadroDialogoRankin;
import com.servfix.manualesapp.interfaces.ApiService;
import com.servfix.manualesapp.network.ApiClient;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    private Boolean isChatAvailable = false;
    private String conversationId = null;
    private int conversationStatus = -1;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_soporte);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityChatSoporteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gv = new GlobalVariables();
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, getBitmapFromEncodedString(manual.imagen_receiver), String.valueOf(id_usuario));
        binding.listviewChat.setAdapter(chatAdapter);
        binding.listviewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        database = FirebaseFirestore.getInstance();
        //getTokerReceiver(manual.id_usuario_firebase);
    }

    private void sendMessage(){
        GlobalVariables gv= new GlobalVariables();
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_ID_USUARIO_MANUAL, String.valueOf(manual.id_usuario_manual));
        message.put(Constants.KEY_ID_USUARIO_ENVIA, String.valueOf(id_usuario));
        message.put(Constants.KEY_ID_USUARIO_RECIBE, String.valueOf(manual.id_usuario_receiver));
        message.put(Constants.KEY_MENSAJE, binding.txtSendMessage.getText().toString());
        message.put(Constants.KEY_FECHA_MENSAJE, new Date());
        database.collection(Constants.KEY_CHATS).add(message);
        if(conversationId != null){
            updateConversion(binding.txtSendMessage.getText().toString());
        }else{
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_ID_USUARIO_MANUAL, String.valueOf(manual.id_usuario_manual));
            conversion.put(Constants.KEY_NOMBRE_MANUAL, manual.nombre_manual);
            conversion.put(Constants.KEY_ID_USUARIO_ENVIA, String.valueOf(preferenceManager.getInt(Constants.KEY_ID_USUARIO)));
            conversion.put(Constants.KEY_NOMBRE_USUARIO_ENVIA, preferenceManager.getString(Constants.KEY_NOMBRE_USUARIO_ENVIA));
            conversion.put(Constants.KEY_IMAGEN, preferenceManager.getString(Constants.KEY_IMAGEN));
            conversion.put(Constants.KEY_ID_USUARIO_FIREBASE, preferenceManager.getString(Constants.KEY_ID_USUARIO_FIREBASE));
            conversion.put(Constants.KEY_ID_USUARIO_RECIBE, String.valueOf(manual.id_usuario_receiver));
            conversion.put(Constants.KEY_NOMBRE_USUARIO_RECIBE, String.valueOf(manual.nombre_usuario_receiver));
            conversion.put(Constants.KEY_IMAGEN_RECIBE, String.valueOf(manual.imagen_receiver));
            conversion.put(Constants.KEY_ID_USUARIO_FIREBASE_RECIBE, manual.id_usuario_firebase_receiver);
            conversion.put(Constants.KEY_ULTIMO_MENSAJE, binding.txtSendMessage.getText().toString());
            conversion.put(Constants.KEY_FECHA_MENSAJE, new Date());
            conversion.put(Constants.KEY_CONVERSATIONS_STATUS, "1");
            addConversion(conversion);
        }
        if(!isReceiverAvailable){
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(receiverToken);

                JSONObject data = new JSONObject();
                data.put("id_usuario_sender", manual.id_usuario_sender);
                data.put("id_usuario_receiver", manual.id_usuario_receiver);
                data.put("id_usuario_manual", String.valueOf(manual.id_usuario_manual));
                data.put("nombre_usuario_sender", manual.nombre_usuario_sender);
                data.put("nombre_usuario_receiver", manual.nombre_usuario_receiver);
                data.put("nombre_manual", manual.nombre_manual);
                data.put("id_usuario_firebase", manual.id_usuario_firebase_receiver);
                data.put("id_usuario_firebase_sender", manual.id_usuario_firebase_sender);
                data.put("tipoNotificacion", "1");
                /*data.put("imagen", manual.imagen_sender);*/
                //data.put("fcmToken", gv.fcmToken);
                data.put("mensaje", binding.txtSendMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

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
        manual = (ClaseChat) getIntent().getSerializableExtra("manual");
        id_usuario = manual.id_usuario_sender;
        binding.txtTituloCursoChat.setText(manual.nombre_manual);
        binding.txtUsuarioChat.setText(manual.nombre_usuario_receiver);
        binding.ivProfileChat.setImageBitmap(getBitmapFromEncodedString(manual.imagen_receiver));

        if(manual.puede_calificar == 1) {
            if (!(manual.calificacion > 0)) {
                calificar();
            }
        }
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

        binding.btnCerrarChat.setOnClickListener(view->{
            SweetAlertDialog sDialog = new SweetAlertDialog(ChatSoporte.this, SweetAlertDialog.WARNING_TYPE);
            sDialog.setTitleText("Desea finalizar la asesoria?");
            //sDialog.setContentText("No se podra recuperar si es eliminada");
            sDialog.setConfirmText("SI");
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    closeConversion();
                }
            })
                    .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    });
            sDialog.show();
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
        database.collection(Constants.KEY_USERS).document(
                manual.id_usuario_firebase_receiver
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
               if(manual.imagen_receiver == null){
                   manual.imagen_sender = value.getString(Constants.KEY_IMAGEN);
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

    private void listenAvailabilityChat(){
        //Constants.KEY_CONVERSATIONS).document(conversationId
        database.collection(Constants.KEY_CONVERSATIONS).document(
                conversationId
        ).addSnapshotListener(ChatSoporte.this, (value, error) ->{
            if(error != null){
                return;
            }
            if(value != null){
                if(value.getString(Constants.KEY_CONVERSATIONS_STATUS) != null){
                    int availability = Integer.parseInt((String) value.getString(Constants.KEY_CONVERSATIONS_STATUS));
                    isChatAvailable = availability == 1;
                }

            }
            if(isChatAvailable){
                binding.layBotonesChat.setVisibility(View.VISIBLE);
            }else{
                binding.layBotonesChat.setVisibility(View.GONE);
            }
        });
    }

    private void listenMessages(){
        database.collection(Constants.KEY_CHATS)
                .whereEqualTo(Constants.KEY_ID_USUARIO_MANUAL, String.valueOf(manual.id_usuario_manual))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        binding.progressBarChat.setVisibility(View.GONE);
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

           if(conversationId == null){
               checkForConversion();
           }



           if(chatMessages.size() > 0)
               binding.listviewChat.smoothScrollToPosition(chatMessages.size() - 1);
      }
    };

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_CONVERSATIONS).document(conversationId);
        documentReference.update(Constants.KEY_ULTIMO_MENSAJE, message, Constants.KEY_FECHA_MENSAJE, new Date());
    }

    private void closeConversion(){
        DocumentReference documentReference =
                database.collection(Constants.KEY_CONVERSATIONS).document(conversationId);
        documentReference.update(Constants.KEY_CONVERSATIONS_STATUS, "0");
        conversationStatus = 0;
        //checkForConversionActive();
    }

    private void checkForConversion(){
        if(chatMessages.size() != 0){
            checkForConversionRemotely(String.valueOf(manual.id_usuario_manual));
        }
    }

    private void checkForConversionRemotely(String id_usuario_manual){
        database.collection(Constants.KEY_CONVERSATIONS)
                .whereEqualTo("id_usuario_manual", id_usuario_manual)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
            conversationStatus = Integer.parseInt((String) documentSnapshot.get(Constants.KEY_CONVERSATIONS_STATUS));
            //checkForConversionActive();
            listenAvailabilityChat();
        }
    };

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
        //listenAvailabilityChat();
    }

    private void calificar(){
        FragmentManager fm = getSupportFragmentManager();

        CuadroDialogoRankin editNameDialogFragment = new CuadroDialogoRankin(getApplicationContext(), manual.id_usuario_manual, manual.id_manual);

        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void checkForConversionActive(){
        if(conversationStatus == 0){
            binding.layBotonesChat.setVisibility(View.GONE);
        }else{
            binding.layBotonesChat.setVisibility(View.VISIBLE);
        }
    }
}