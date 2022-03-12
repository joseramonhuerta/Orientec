package com.servfix.manualesapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ListviewItemsConversacionesBinding;

public class ConversacionesAdapter {
    class ConversacionViewHolder extends RecyclerView.ViewHolder{

        ListviewItemsConversacionesBinding binding;

        ConversacionViewHolder(ListviewItemsConversacionesBinding listviewItemsConversacionesBinding){
            super(listviewItemsConversacionesBinding.getRoot());
            binding =  listviewItemsConversacionesBinding;
        }

        void setData(Manual manual){
            

        }
    }
}
