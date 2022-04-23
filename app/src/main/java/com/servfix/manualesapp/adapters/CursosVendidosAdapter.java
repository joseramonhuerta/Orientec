package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ListviewItemCursovendidoBinding;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CursosVendidosAdapter extends RecyclerView.Adapter<CursosVendidosAdapter.CursosVendidosViewHolder>{

    private final List<Manual> manualList;
    private Context context;

    public CursosVendidosAdapter(List<Manual> manualList, Context context){
        this.manualList = manualList;
        this.context = context;
    }

    @Override
    public CursosVendidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new CursosVendidosViewHolder(
                ListviewItemCursovendidoBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CursosVendidosAdapter.CursosVendidosViewHolder holder, int position){
        holder.setData(manualList.get(position));
    }

    @Override
    public int getItemCount(){
        return manualList.size();
    }

    class CursosVendidosViewHolder extends RecyclerView.ViewHolder{

        ListviewItemCursovendidoBinding binding;

        CursosVendidosViewHolder(ListviewItemCursovendidoBinding itemCursovendidoBinding){
            super(itemCursovendidoBinding.getRoot());
            binding = itemCursovendidoBinding;
        }

        void setData(Manual manual){
            Picasso.get().load(manual.getPortada())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivImagenCursoVendido);

            binding.txtNombreCursoVendido.setText(manual.getNombre_manual());
            binding.txtCategoriaCursoVendido.setText(manual.getNombre_categoria());
            binding.txtPrecioCursoVendido.setText("$ " + getPrecioFormatoMoneda(manual.getPrecio()));
        }
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }



}