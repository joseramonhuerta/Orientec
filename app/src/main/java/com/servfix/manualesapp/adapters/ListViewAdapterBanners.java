package com.servfix.manualesapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.servfix.manualesapp.classes.Banner;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.interfaces.ApiService;
import com.servfix.manualesapp.network.ApiClient;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListViewAdapterBanners extends BaseAdapter
{
    Context context;

    List<Banner> TempBannerList;

    Dialog customDialog = null;


    public ListViewAdapterBanners(List<Banner> listValue, Context context)
    {
        this.context = context;
        this.TempBannerList = listValue;

    }

    @Override
    public int getCount()
    {
        return this.TempBannerList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempBannerList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemBanner viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemBanner();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_item_banner, parent, false);

            viewItem.ivBanner = (ImageView) convertView.findViewById(R.id.ivImagenBanner);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemBanner) convertView.getTag();
        }


        Picasso.get().load(TempBannerList.get(position).getImagen_banner())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivBanner);

        //viewItem.ivBanner.setImageBitmap(getBitmapFromEncodedString(TempBannerList.get(position).getImagen_banner()));

        return convertView;
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

class ViewItemBanner
{
   ImageView ivBanner;
}
