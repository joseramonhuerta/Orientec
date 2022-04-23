package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.MensajeChat;
import com.servfix.manualesapp.classes.ItemEstadoCuenta;
import com.servfix.manualesapp.databinding.ListviewItemEstadocuentaBinding;
import com.servfix.manualesapp.listeners.ConversacionesListerner;

import java.text.DecimalFormat;
import java.util.List;

public class EstadoCuentaAdapter extends RecyclerView.Adapter<EstadoCuentaAdapter.EstadoCuentaViewHolder>{

    private final List<ItemEstadoCuenta> itemsEstadoCuenta;
    private Context context;

    public EstadoCuentaAdapter(List<ItemEstadoCuenta> itemsEstadoCuenta, Context context){
        this.itemsEstadoCuenta = itemsEstadoCuenta;
        this.context = context;
    }

    @Override
    public EstadoCuentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new EstadoCuentaViewHolder(
                ListviewItemEstadocuentaBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EstadoCuentaAdapter.EstadoCuentaViewHolder holder, int position){
        holder.setData(itemsEstadoCuenta.get(position));
    }

    @Override
    public int getItemCount(){
        return itemsEstadoCuenta.size();
    }

    class EstadoCuentaViewHolder extends RecyclerView.ViewHolder{

        ListviewItemEstadocuentaBinding binding;

        EstadoCuentaViewHolder(ListviewItemEstadocuentaBinding itemContainerEstadoCuentaBinding){
            super(itemContainerEstadoCuentaBinding.getRoot());
            binding = itemContainerEstadoCuentaBinding;
        }

        void setData(ItemEstadoCuenta itemEstadoCuenta){
            binding.txtFechaDetalleEstadoCuenta.setText(itemEstadoCuenta.Fecha);
            binding.txtConceptoDetalleEstadoCuenta.setText(itemEstadoCuenta.Concepto);
            binding.txtCargoDetalleEstadoCuenta.setText("$ " + getPrecioFormatoMoneda(itemEstadoCuenta.Cargo));
            binding.txtAbonoDetalleEstadoCuenta.setText("$ " + getPrecioFormatoMoneda(itemEstadoCuenta.Abono));
            binding.txtSaldoDetalleEstadoCuenta.setText("$ " + getPrecioFormatoMoneda(itemEstadoCuenta.Saldo));

        }
        /*id_conversacion, nombre_conversacion, imagen_conversacion, nombre_manual_conversacion, fecha_conversacion,ultimo_mensaje*/
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }
}

