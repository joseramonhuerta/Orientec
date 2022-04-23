package com.servfix.manualesapp.adapters;

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

import com.bumptech.glide.Glide;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Categoria;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterCategoriasBusqueda extends BaseAdapter
{
    Context context;
    List<Categoria> TempCategoriasList;
    List<Categoria> TempCategoriasListFilter;


    public ListViewAdapterCategoriasBusqueda(List<Categoria> listValue, Context context)
    {
        this.context = context;
        this.TempCategoriasList = listValue;

        this.TempCategoriasListFilter = new ArrayList<Categoria>();
        this.TempCategoriasListFilter.addAll(TempCategoriasList);
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
        ViewItemCategoriaBusqueda viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemCategoriaBusqueda();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_item_categoria, parent, false);


            viewItem.txtNombreCategoria = (TextView)convertView.findViewById(R.id.txtNombreCategoriaBusqueda);

            viewItem.ivImagenCategoria = (ImageView) convertView.findViewById(R.id.ivCategoriaBusqueda);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCategoriaBusqueda) convertView.getTag();
        }


        viewItem.txtNombreCategoria.setText(TempCategoriasList.get(position).getNombre_categoria());
        /*Picasso.get().load(TempCategoriasList.get(position).getIcono())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivImagenCategoria);*/
        viewItem.ivImagenCategoria.setImageBitmap(getConversionImage(TempCategoriasList.get(position).getImagen_categoria()));


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempCategoriasList.clear();
        if (charText.length() == 0) {
            TempCategoriasList.addAll(TempCategoriasListFilter);
        }
        else
        {
            for (Categoria wp : TempCategoriasListFilter) {
                if (wp.getNombre_categoria().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempCategoriasList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}

class ViewItemCategoriaBusqueda
{

    TextView txtNombreCategoria;


    ImageView ivImagenCategoria;


}
