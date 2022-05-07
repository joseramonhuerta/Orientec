package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ListviewItemCursovendidoBinding;
import com.servfix.manualesapp.databinding.ListviewItemInformacionusuarioBinding;
import com.servfix.manualesapp.interfaces.ApiService;
import com.servfix.manualesapp.listeners.ConversacionesListerner;
import com.servfix.manualesapp.listeners.InformacionUsuarioListener;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InformacionUsuarioAdapter extends RecyclerView.Adapter<InformacionUsuarioAdapter.InformacionUsuarioViewHolder>{

    private final List<Manual> manualList;
    private Context context;
    private final InformacionUsuarioListener informacionUsuarioListener;

    public InformacionUsuarioAdapter(List<Manual> manualList, Context context, InformacionUsuarioListener informacionUsuarioListener){
        this.manualList = manualList;
        this.context = context;
        this.informacionUsuarioListener = informacionUsuarioListener;
    }

    @Override
    public InformacionUsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new InformacionUsuarioViewHolder(
                ListviewItemInformacionusuarioBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull InformacionUsuarioAdapter.InformacionUsuarioViewHolder holder, int position){
        holder.setData(manualList.get(position));
    }

    @Override
    public int getItemCount(){
        return manualList.size();
    }

    class InformacionUsuarioViewHolder extends RecyclerView.ViewHolder{

        ListviewItemInformacionusuarioBinding binding;

        InformacionUsuarioViewHolder(ListviewItemInformacionusuarioBinding itemInformacionusuarioBinding){
            super(itemInformacionusuarioBinding.getRoot());
            binding = itemInformacionusuarioBinding;
        }

        void setData(Manual manual){

            Picasso.get().load(manual.getUrl_portada())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivImagenInformacionUsuario);

            //binding.ivImagenInformacionUsuario.setImageBitmap(getBitmapFromEncodedString(manual.getPortada()));

            binding.txtNombreInformacionUsuario.setText(manual.getNombre_manual());
            binding.txtDescripcionInformacionUsuario.setText(manual.getDescripcion_manual());
            binding.txtPaginasInformacionUsuario.setText(manual.getPaginas());
            binding.txtCalificacionInformacionUsuario.setText(String.valueOf(manual.getCalificacion()));
            binding.txtCategoriaInformacionUsuario.setText(manual.getNombre_categoria());
            if(manual.getEsgratuito() == 1){
                binding.txtPrecioInformacionUsuario.setVisibility(View.GONE);
                binding.txtGratuitoInformacionUsuario.setVisibility(View.VISIBLE);
            }else {
                binding.txtPrecioInformacionUsuario.setVisibility(View.VISIBLE);
                binding.txtPrecioInformacionUsuario.setText("$ " + getPrecioFormatoMoneda(manual.getPrecio()));
                binding.txtGratuitoInformacionUsuario.setVisibility(View.GONE);
            }
            binding.getRoot().setOnClickListener(v -> {

                informacionUsuarioListener.onInformacionUsuarioClicked(manual);

            });

        }
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }

}
