package com.servfix.manualesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.servfix.manualesapp.DetalleManual;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
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
            viewItem.btnComprarCurso = (Button)convertView.findViewById(R.id.btnComprarCurso);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCurso) convertView.getTag();
        }

        viewItem.txtTituloCurso.setText(manualesArray.get(position).getNombre_manual());
        viewItem.txtPaginasCurso.setText(manualesArray.get(position).getPaginas());
        viewItem.txtDescripcionCurso.setText(manualesArray.get(position).getDescripcion_manual());
        viewItem.txtPrecioCurso.setText("$ " + String.valueOf(manualesArray.get(position).getPrecio()));
        viewItem.ivImagenCurso.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //viewItem.ivImagenPagina.setLayoutParams(new GridView.LayoutParams(340, 350));

        viewItem.btnComprarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Manual manual = (Manual) manualesArray.get(position);

                Intent intencion = new Intent(mContext, DetalleManual.class);
                intencion.putExtra("manual", (Serializable)manual);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intencion);


            }
        });

        Picasso.get().load(manualesArray.get(position).getPortada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivImagenCurso);

        return convertView;


    }
}

class ViewItemCurso
{
    TextView txtTituloCurso;
    TextView txtDescripcionCurso;
    TextView txtPaginasCurso;
    TextView txtPrecioCurso;

    ImageView ivImagenCurso;

    Button btnComprarCurso;



}