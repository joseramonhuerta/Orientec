package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.servfix.manualesapp.DetalleManual;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class GridViewAdapterCursos extends BaseAdapter {

    Context mContext;

    List<Manual> manualesArray;

    private FragmentManager fm;
    private Fragment mainFrag;

    public GridViewAdapterCursos(List<Manual> listValue, Context mContext/*, FragmentManager fm,Fragment mainFrag*/) {
        this.mContext = mContext;
        this.manualesArray = listValue;
        /*this.fm = fm;
        this.mainFrag = mainFrag;*/
    }

    @Override
    public int getCount() {
        return this.manualesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return this.manualesArray.get(position);
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

        ViewItemCurso viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemCurso();

            LayoutInflater layoutInfiater = LayoutInflater.from(mContext);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.gridview_item_curso, parent, false);


            viewItem.ivImagenCurso = (ImageView) convertView.findViewById(R.id.ivImagenCurso);
            viewItem.txtTituloCurso = (TextView)convertView.findViewById(R.id.txtTituloCurso);
            viewItem.txtPaginasCurso = (TextView)convertView.findViewById(R.id.txtPaginasCurso);
            viewItem.txtDescripcionCurso = (TextView)convertView.findViewById(R.id.txtDescripcionCurso);
            viewItem.txtPrecioCurso = (TextView)convertView.findViewById(R.id.txtPrecioCurso);
            viewItem.txtGratuitoCurso = (TextView)convertView.findViewById(R.id.txtGratuitoCurso);
            viewItem.btnComprarCurso = (Button)convertView.findViewById(R.id.btnComprarCurso);
            viewItem.txtCalificacion = (TextView)convertView.findViewById(R.id.txtCalificacionCurso);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCurso) convertView.getTag();
        }

        viewItem.txtTituloCurso.setText(manualesArray.get(position).getNombre_manual());
        viewItem.txtPaginasCurso.setText(manualesArray.get(position).getPaginas());
        viewItem.txtDescripcionCurso.setText(manualesArray.get(position).getDescripcion_manual());
        if(manualesArray.get(position).getEsgratuito() == 1){
            viewItem.txtPrecioCurso.setVisibility(View.GONE);
            viewItem.txtGratuitoCurso.setVisibility(View.VISIBLE);
        }else{
            viewItem.txtPrecioCurso.setText("$ " + getPrecioFormatoMoneda(manualesArray.get(position).getPrecio()));
        }

        viewItem.ivImagenCurso.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewItem.txtCalificacion.setText(String.valueOf(manualesArray.get(position).getCalificacion()));
        //viewItem.ivImagenPagina.setLayoutParams(new GridView.LayoutParams(340, 350));

        viewItem.btnComprarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manual manual = (Manual) manualesArray.get(position);

                Intent intencion = new Intent(mContext, DetalleManual.class);
                intencion.putExtra("manual", (Serializable)manual);
                intencion.putExtra("perfilUsuario", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intencion);


            }
        });
        /*
        Picasso.get().load(manualesArray.get(position).getPortada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivImagenCurso);*/

        viewItem.ivImagenCurso.setImageBitmap(getBitmapFromEncodedString(manualesArray.get(position).getPortada()));

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

class ViewItemCurso
{
    TextView txtTituloCurso;
    TextView txtDescripcionCurso;
    TextView txtPaginasCurso;
    TextView txtPrecioCurso;
    TextView txtGratuitoCurso;
    TextView txtCalificacion;
    ImageView ivImagenCurso;

    Button btnComprarCurso;



}
