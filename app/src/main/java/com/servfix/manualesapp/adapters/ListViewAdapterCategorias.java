package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.servfix.manualesapp.classes.Categoria;
import com.servfix.manualesapp.R;

import java.util.List;

public class ListViewAdapterCategorias extends BaseAdapter
{
    Context context;
    List<Categoria> TempCategoriasList;



    public ListViewAdapterCategorias(List<Categoria> listValue, Context context)
    {
        this.context = context;
        this.TempCategoriasList = listValue;
    }

    @Override
    public int getCount()
    {
        return this.TempCategoriasList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempCategoriasList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemCategoria viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemCategoria();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_categorias, parent, false);


            viewItem.txtNombreCategoria = (TextView)convertView.findViewById(R.id.txtNombreCategoria);

            viewItem.ivImagenCategoria = (ImageView) convertView.findViewById(R.id.ivImagenCategoria);
            String icono = "(R.drawable." + TempCategoriasList.get(position).getIcono();
            int id_icono = context.getResources().getIdentifier(TempCategoriasList.get(position).getIcono(), "drawable", context.getPackageName());
            viewItem.ivImagenCategoria.setImageResource(id_icono);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCategoria) convertView.getTag();
        }


        viewItem.txtNombreCategoria.setText(TempCategoriasList.get(position).getNombre_categoria());





        return convertView;
    }



}

class ViewItemCategoria
{

    TextView txtNombreCategoria;


    ImageView ivImagenCategoria;


}
