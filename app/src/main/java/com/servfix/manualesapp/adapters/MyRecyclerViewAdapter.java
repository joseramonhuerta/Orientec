package com.servfix.manualesapp.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.classes.Categoria;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.interfaces.RecyclerViewOnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private List<Categoria> data;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    private Context context;

    public MyRecyclerViewAdapter(@NonNull List<Categoria> data,
                                 @NonNull RecyclerViewOnItemClickListener
                                         recyclerViewOnItemClickListener, Context context) {
        this.data = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_items_categorias, parent, false);
        return new MyViewHolder(row);
    }
    private final ArrayList selected = new ArrayList<>();
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Categoria categoria = data.get(position);
        holder.getNameTextView().setText(categoria.getNombre_categoria());
        //holder.getPersonImageView().setImageResource(R.drawable.selector_tecnicos);
        //set image with picasso.
        //permission required : android.permission.INTERNET
        if(categoria.getId_categoria() == 0){
            Picasso.get()
                    .load(categoria.getIcono())
                    .into(holder.getPersonImageView());
        }else{
            holder.getPersonImageView().setImageBitmap(getConversionImage(categoria.getImagen_categoria()));
        }



        /*
        if (!selected.contains(position)){
            holder.getPersonImageView().setImageResource(R.drawable.tecnic);
        } else
            holder.getPersonImageView().setImageResource(R.drawable.tecnic_selected);
        */

        //int id_icono = context.getResources().getIdentifier(data.get(position).getIcono(), "drawable", context.getPackageName());
        //ivImagenCategoria.setImageResource(id_icono);



    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView txtNombreCategoria;
        private ImageView ivImagenCategoria;


        public MyViewHolder(View itemView) {
            super(itemView);
            txtNombreCategoria = (TextView) itemView.findViewById(R.id.txtNombreCategoria);
            ivImagenCategoria = (ImageView) itemView.findViewById(R.id.ivImagenCategoria);

            itemView.setOnClickListener(this);
        }

        public TextView getNameTextView() {
            return txtNombreCategoria;
        }

        public ImageView getPersonImageView() {
            return ivImagenCategoria;
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            //ivImagenCategoria = (ImageView) itemView.findViewById(R.id.ivImagenCategoria);
            //int id_icono = context.getResources().getIdentifier(data.get(position).getIcono(), "drawable", context.getPackageName());
            //ivImagenCategoria.setImageResource(id_icono);
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());

            if (selected.isEmpty()) {
                selected.add(position);
            } else {
                int oldSelected = (int) selected.get(0);
                selected.clear();
                selected.add(position);
                notifyItemChanged(oldSelected);
            }
            // we do not notify that an item has been selected
            // because that work is done here. we instead send
            // notifications for items to be deselected


        }

    }

}