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

import java.util.List;

public class ListViewAdapterChat extends BaseAdapter
{
    Context context;

    List<Manual> TempManualList;

    Dialog customDialog = null;


    public ListViewAdapterChat(List<Manual> listValue, Context context)
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
        ViewItemChat viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemChat();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_chats, parent, false);

            viewItem.txtNombreUsuario = (TextView)convertView.findViewById(R.id.txtNombreUsuario);
            viewItem.txtNombreManual = (TextView)convertView.findViewById(R.id.txtNombreManual);
            viewItem.ivImagenUsuario = (ImageView) convertView.findViewById(R.id.ivImagenUsuario);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemChat) convertView.getTag();
        }

        viewItem.txtNombreUsuario.setText(String.valueOf(TempManualList.get(position).getNombre_usuario()));
        viewItem.txtNombreManual.setText(String.valueOf(TempManualList.get(position).getNombre_manual()));

        byte[] bytes = Base64.decode(TempManualList.get(position).getImagen_usuario(), Base64.DEFAULT);
        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        viewItem.ivImagenUsuario.setImageBitmap(img);

        return convertView;
    }



}

class ViewItemChat
{
    TextView txtNombreManual;
    TextView txtNombreUsuario;
    ImageView ivImagenUsuario;



}