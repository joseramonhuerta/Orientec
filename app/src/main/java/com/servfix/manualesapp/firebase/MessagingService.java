package com.servfix.manualesapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.servfix.manualesapp.ChatSoporte;
import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.MensajeChat;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.activities.MenuPrincipal;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("FirebaseMessaging", "onMessageReceived");
        if (remoteMessage.getData().size()>0){

            int opcion = Integer.parseInt(remoteMessage.getData().get("tipoNotificacion"));

            if( opcion== 1)
            {
                notificacionChat(remoteMessage);
            }

            if(opcion == 2)
            {
                notificacionCursos(remoteMessage);
            }


        }


    }

    public void notificacionChat(RemoteMessage remoteMessage){
        Log.i("FirebaseMessaging", "notificacionChat");

        ClaseChat chat = new ClaseChat();
        chat.id_usuario_sender = Integer.parseInt(remoteMessage.getData().get("id_usuario_receiver"));
        chat.id_usuario_receiver = Integer.parseInt(remoteMessage.getData().get("id_usuario_sender"));
        chat.id_usuario_manual = Integer.parseInt(remoteMessage.getData().get("id_usuario_manual"));
        chat.nombre_usuario_sender = remoteMessage.getData().get("nombre_usuario_receiver");
        chat.nombre_usuario_receiver = remoteMessage.getData().get("nombre_usuario_sender");
        chat.nombre_manual = remoteMessage.getData().get("nombre_manual");
        /*chat.imagen = remoteMessage.getData().get("imagen");*/
        chat.id_usuario_firebase_receiver = remoteMessage.getData().get("id_usuario_firebase_sender");
        chat.id_usuario_firebase_sender = remoteMessage.getData().get("id_usuario_firebase");
        chat.token = remoteMessage.getData().get("fcmToken");

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, ChatSoporte.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("manual", (Serializable) chat);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification_orientec);
        builder.setContentTitle(chat.nombre_usuario_receiver);
        builder.setContentText(remoteMessage.getData().get("mensaje"));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get("mensaje")
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("FirebaseMessaging", "Build.VERSION_CODES.O");
            CharSequence channelName = "Chat Message";
            String channelDescription = "This is the channer description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
        Log.i("FirebaseMessaging", "Notification Sended");
    }

    public void notificacionCursos(RemoteMessage remoteMessage){
        boolean conFoto = true;
        int notificationId = new Random().nextInt();
        String channelId = "mensaje";
        Bitmap imf_foto = null;
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        try {

            try {
                imf_foto= Picasso.get().load(remoteMessage.getData().get("foto")).get();
            } catch (IOException e) {
               conFoto = false;

            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            builder.setSmallIcon(R.drawable.ic_notification_orientec);
            builder.setContentTitle(remoteMessage.getData().get("titulo"));
            builder.setContentText(remoteMessage.getData().get("detalle"));
            if (conFoto)
            {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(imf_foto).bigLargeIcon(null));
            }
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("FirebaseMessaging", "Build.VERSION_CODES.O");
                CharSequence channelName = channelId;
                String channelDescription = "This is the channer description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                channel.setDescription(channelDescription);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(notificationId, builder.build());
            Log.i("FirebaseMessaging", "Notification Sended");
        } catch (Exception e) {
            Log.i("FirebaseMessaging", "IOException");
            e.printStackTrace();

        }
    }
}
