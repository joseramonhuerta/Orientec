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
import com.servfix.manualesapp.activities.CursosVendidos;
import com.servfix.manualesapp.classes.Banner;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityCursosVendidosBinding;
import com.servfix.manualesapp.databinding.ListviewItemBannerBinding;
import com.servfix.manualesapp.databinding.ListviewItemCursovendidoBinding;
import com.servfix.manualesapp.databinding.ListviewItemInformacionusuarioBinding;
import com.servfix.manualesapp.interfaces.ApiService;
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

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.BannersViewHolder>{

    private final List<Banner> bannersList;
    private Context context;

    public BannersAdapter(List<Banner> bannersList, Context context){
        this.bannersList = bannersList;
        this.context = context;
    }

    @Override
    public BannersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new BannersViewHolder(
                ListviewItemBannerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BannersAdapter.BannersViewHolder holder, int position){
        holder.setData(bannersList.get(position));
    }

    @Override
    public int getItemCount(){
        return bannersList.size();
    }

    class BannersViewHolder extends RecyclerView.ViewHolder{

        ListviewItemBannerBinding binding;

        BannersViewHolder(ListviewItemBannerBinding itemBannerBinding){
            super(itemBannerBinding.getRoot());
            binding = itemBannerBinding;
        }

        void setData(Banner banner){

            Picasso.get().load(banner.getImagen_banner())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivImagenBanner);

            //binding.ivImagenBanner.setImageBitmap(getBitmapFromEncodedString(banner.getImagen_banner()));


        }
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }else{
            return null;
        }
    }


}
