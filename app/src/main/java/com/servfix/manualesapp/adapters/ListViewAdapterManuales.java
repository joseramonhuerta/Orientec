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

public class ListViewAdapterManuales extends BaseAdapter
{
    Context context;

    List<Manual> TempManualList;

    Dialog customDialog = null;


    public ListViewAdapterManuales(List<Manual> listValue, Context context)
    {
        this.context = context;
        this.TempManualList = listValue;

    }

    @Override
    public int getCount()
    {
        return this.TempManualList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempManualList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemManual viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemManual();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_manuales, parent, false);

            viewItem.txtNombre_manual = (TextView)convertView.findViewById(R.id.txtNombreManual);
            viewItem.txtDescripcion_manual = (TextView)convertView.findViewById(R.id.txtDescripcionManual);
            viewItem.txtNum_paginas = (TextView)convertView.findViewById(R.id.txtNumPaginas);

            viewItem.ivPortada = (ImageView) convertView.findViewById(R.id.ivManual);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemManual) convertView.getTag();
        }

        viewItem.txtNombre_manual.setText(String.valueOf(TempManualList.get(position).getNombre_manual()));
        viewItem.txtDescripcion_manual.setText(String.valueOf(TempManualList.get(position).getDescripcion_manual()));
        viewItem.txtNum_paginas.setText(TempManualList.get(position).getPaginas());

        Picasso.get().load(TempManualList.get(position).getUrl_portada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivPortada);

        return convertView;
    }
}

class ViewItemManual
{
    TextView txtNombre_manual;
    TextView txtDescripcion_manual;
    TextView txtNum_paginas;
    ImageView ivPortada;



}
