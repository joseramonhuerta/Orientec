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

import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Manual;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class ListViewAdapterManualesTecnico extends BaseAdapter
{
    Context context;

    List<Manual> TempManualList;

    Dialog customDialog = null;


    public ListViewAdapterManualesTecnico(List<Manual> listValue, Context context)
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
        ViewItemManualTecnico viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemManualTecnico();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_manuales_tecnico, parent, false);

            viewItem.txtNombre_manual = (TextView)convertView.findViewById(R.id.txtNombreManualTecnico);
            viewItem.txtDescripcion_manual = (TextView)convertView.findViewById(R.id.txtDescripcionManualTecnico);
            viewItem.txtNum_paginas = (TextView)convertView.findViewById(R.id.txtNumPaginasTecnico);
            viewItem.txtPrecio = (TextView)convertView.findViewById(R.id.txtPrecioManualTecnico);

            viewItem.ivPortada = (ImageView) convertView.findViewById(R.id.ivManualTecnico);
            viewItem.ivStatusManual = (ImageView) convertView.findViewById(R.id.ivStatusManual);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemManualTecnico) convertView.getTag();
        }

        viewItem.txtNombre_manual.setText(String.valueOf(TempManualList.get(position).getNombre_manual()));
        viewItem.txtDescripcion_manual.setText(String.valueOf(TempManualList.get(position).getDescripcion_manual()));
        viewItem.txtNum_paginas.setText(TempManualList.get(position).getPaginas());
        String precioString = "$ " + getPrecioFormatoMoneda(TempManualList.get(position).getPrecio());
        viewItem.txtPrecio.setText(precioString);
        Picasso.get().load(TempManualList.get(position).getUrl_portada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivPortada);

        if(TempManualList.get(position).getStatus_manual() == 1)
            viewItem.ivStatusManual.setImageResource(R.drawable.toggle_on);
        else
            viewItem.ivStatusManual.setImageResource(R.drawable.toggle_off);


        return convertView;
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
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

class ViewItemManualTecnico
{
    TextView txtNombre_manual;
    TextView txtDescripcion_manual;
    TextView txtNum_paginas;
    TextView txtPrecio;
    ImageView ivPortada;
    ImageView ivStatusManual;



}
