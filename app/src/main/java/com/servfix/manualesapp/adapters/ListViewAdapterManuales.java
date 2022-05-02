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

import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewAdapterManuales extends BaseAdapter
{
    Context context;

    List<Manual> TempManualList;

    Dialog customDialog = null;


    public ListViewAdapterManuales(List<Manual> listValue, Context context)
    {
        this.context = context;
        this.TempManualList = listValue;

        //this.TempGastosListFilter = new ArrayList<Gasto>();
        //this.TempGastosListFilter.addAll(TempGastosList);


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
        /*
        Picasso.get().load(TempManualList.get(position).getPortada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivPortada);
        */

        viewItem.ivPortada.setImageBitmap(getBitmapFromEncodedString(TempManualList.get(position).getImagen_detalle()));

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

class ViewItemManual
{
    TextView txtNombre_manual;
    TextView txtDescripcion_manual;
    TextView txtNum_paginas;
    ImageView ivPortada;



}
