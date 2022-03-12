package com.servfix.manualesapp;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.databinding.ItemContainerSendMessageBinding;

import java.util.List;
import com.servfix.manualesapp.databinding.ItemContainerReceivedMessageBinding;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<MensajeChat> data;
    private Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SEND = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(List<MensajeChat> data, Bitmap receiverProfileImage, String senderId) {
        this.data = data;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND){
            return new SentMessageViewHolder(
                    ItemContainerSendMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false
                    )
            );
        }else{
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SEND){
            ((SentMessageViewHolder) holder).setData(data.get(position));
        }else{
            ((ReceivedMessageViewHolder) holder).setData(data.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).id_usuario_envia.equals(senderId)){
            return VIEW_TYPE_SEND;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSendMessageBinding binding;

        SentMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding){
            super(itemContainerSendMessageBinding.getRoot());
            binding = itemContainerSendMessageBinding;
        }

        void setData(MensajeChat mensajeChat){
            binding.txtMensaje.setText(mensajeChat.mensaje);
            binding.txtFechaHoraMensaje.setText(mensajeChat.fecha_mensaje_formateada);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(MensajeChat mensajeChat, Bitmap bitmap){
            binding.txtMensaje.setText(mensajeChat.mensaje);
            binding.txtFechaHoraMensaje.setText(mensajeChat.fecha_mensaje_formateada);
            if(bitmap != null) {
                binding.ivProfileMessage.setImageBitmap(bitmap);
            }
        }

    }

}
