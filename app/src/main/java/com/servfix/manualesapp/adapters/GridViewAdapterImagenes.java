package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.servfix.manualesapp.Pagina;
import com.servfix.manualesapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridViewAdapterImagenes extends BaseAdapter {

    Context mContext;

    List<Pagina> imagenesArray;

    public GridViewAdapterImagenes(List<Pagina> listValue, Context mContext) {
        this.mContext = mContext;
        this.imagenesArray = listValue;
    }

    @Override
    public int getCount() {
        return imagenesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return this.imagenesArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*ImageView imageView =  new ImageView(mContext);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(340, 350));

        Picasso.get().load(imagenesArray.get(position).toString())
               .error(R.drawable.ic_baseline_broken_image_24)
               .into(imageView);

        return imageView;*/

        ViewItemPagina viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemPagina();

            LayoutInflater layoutInfiater = LayoutInflater.from(mContext);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.gridview_item, parent, false);


            viewItem.ivImagenPagina = (ImageView) convertView.findViewById(R.id.ivImagenPagina);
            viewItem.txtTituloPagina = (TextView)convertView.findViewById(R.id.txtTituloPaginaItem);
            viewItem.txtNumeroPagina = (TextView)convertView.findViewById(R.id.txtNumeroPaginaItem);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemPagina) convertView.getTag();
        }

        viewItem.txtTituloPagina.setText(imagenesArray.get(position).getTitulo_pagina());
        viewItem.txtNumeroPagina.setText(imagenesArray.get(position).getNumero_pagina());
        viewItem.ivImagenPagina.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //viewItem.ivImagenPagina.setLayoutParams(new GridView.LayoutParams(340, 350));

        Picasso.get().load(imagenesArray.get(position).getUrl_pagina())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivImagenPagina);

        return convertView;


    }
}

class ViewItemPagina
{
    TextView txtTituloPagina;
    TextView txtNumeroPagina;
    ImageView ivImagenPagina;



}
