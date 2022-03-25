 package com.servfix.manualesapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.MensajeChat;
import com.servfix.manualesapp.databinding.ListviewItemsConversacionesBinding;
import com.servfix.manualesapp.listeners.ConversacionesListerner;

import java.io.Serializable;
import java.util.List;

public class ConversacionesAdapter extends RecyclerView.Adapter<ConversacionesAdapter.ConversionViewHolder>{

    private final List<MensajeChat> chatMessages;
    private final ConversacionesListerner conversacionesListerner;

    public ConversacionesAdapter(List<MensajeChat> chatMessages, ConversacionesListerner conversacionesListerner){
        this.chatMessages = chatMessages;
        this.conversacionesListerner = conversacionesListerner;
    }

    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ConversionViewHolder(
                ListviewItemsConversacionesBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversacionesAdapter.ConversionViewHolder holder, int position){
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount(){
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ListviewItemsConversacionesBinding binding;

        ConversionViewHolder(ListviewItemsConversacionesBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(MensajeChat chatMessage){
            binding.ivImagenUsuarioConversacion.setImageBitmap(getConversionImage(chatMessage.imagen_conversacion));
            binding.txtNombreUsuarioConversacion.setText(chatMessage.nombre_usuario_recibe);
            binding.txtNombreManualConversacion.setText(chatMessage.nombre_manual_conversacion);
            binding.txtRecentMessageConversacion.setText(chatMessage.ultimo_mensaje);
            binding.txtRecentDateConversacion.setText(chatMessage.fecha_conversacion);
            binding.getRoot().setOnClickListener(v -> {
                ClaseChat chat = new ClaseChat();
                chat.id_usuario_manual = Integer.parseInt(chatMessage.id_usuario_manual);
                chat.id_usuario_sender =  Integer.parseInt(chatMessage.id_usuario_recibe);
                chat.id_usuario_receiver =  Integer.parseInt(chatMessage.id_usuario_envia);
                chat.nombre_manual = chatMessage.nombre_manual_conversacion;
                //chat.nombre_usuario_sender = chatMessage.nombre_usuario_conversacion;
                chat.nombre_usuario_receiver = chatMessage.nombre_usuario_recibe;
                chat.nombre_usuario_sender = chatMessage.nombre_usuario_envia;
                chat.imagen_receiver = chatMessage.imagen_conversacion;
                chat.imagen_sender = chatMessage.imagen_conversacion;
                chat.id_usuario_firebase_receiver = chatMessage.id_usuario_firebase_recibe;
                chat.id_usuario_firebase_sender = chatMessage.id_usuario_firebase_envia;
                conversacionesListerner.onConversionClicked(chat);

            });
        }
        /*id_conversacion, nombre_conversacion, imagen_conversacion, nombre_manual_conversacion, fecha_conversacion,ultimo_mensaje*/
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
