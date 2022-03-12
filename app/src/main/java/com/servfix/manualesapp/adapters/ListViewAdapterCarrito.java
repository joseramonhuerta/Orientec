package com.servfix.manualesapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.classes.Carrito;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListViewAdapterCarrito extends BaseAdapter
{
    Context context;
    SweetAlertDialog pDialogo;
    String FinalJSonObject;
    List<Carrito> TempCarritoList;
    View mView;

    Dialog customDialog = null;

    private AdapterCallback adapterCallback;

    public interface AdapterCallback {
        void onMethodCallback(View mView);

    }

    public ListViewAdapterCarrito(List<Carrito> listValue, Context context, View view)
    {
        this.context = context;
        this.TempCarritoList = listValue;
        this.mView = view;
        try {
            adapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }

    }

    @Override
    public int getCount()
    {
        return this.TempCarritoList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempCarritoList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemCarrito viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemCarrito();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_item_carrito, parent, false);

            viewItem.txtNombre_manual = (TextView)convertView.findViewById(R.id.txtNombreManualCarrito);
            viewItem.txtPrecio_manual = (TextView)convertView.findViewById(R.id.txtPrecioManualCarrito);
            viewItem.ivImagenManual = (ImageView) convertView.findViewById(R.id.ivImagenManualCarrito);
            viewItem.btnEliminar = (Button) convertView.findViewById(R.id.btnEliminarManualCarrito);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCarrito) convertView.getTag();
        }

        viewItem.txtNombre_manual.setText(String.valueOf(TempCarritoList.get(position).getNombre_manual()));
        viewItem.txtPrecio_manual.setText("$ " + String.valueOf(TempCarritoList.get(position).getPrecio()));
        Picasso.get().load(TempCarritoList.get(position).getPortada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(viewItem.ivImagenManual);

        viewItem.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Preguntar si desea eliminar el detalla*/
                //notifyDataSetChanged();
                SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Desea eliminar el detalle?");
                //sDialog.setContentText("No se podra recuperar si es eliminada");
                sDialog.setConfirmText("SI");
                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        eliminarItemCarrito(v, position);
                    }
                })
                        .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        });
                sDialog.show();
            }
        });



        return convertView;
    }

    public void eliminarItemCarrito(View view, int position){
        final View vista = view;
        pDialogo = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();


        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;


            HTTP_URL =variablesGlobales.URLServicio + "eliminardetallecarrito.php?id_carrito_compras_detalle=" + String.valueOf(TempCarritoList.get(position).getId_carrito_compras_detalle());
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new ListViewAdapterCarrito.ParseJSonDataClassEliminar(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(view.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

            // Creating String Request Object.
            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

            // Passing String request into RequestQueue.
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ParseJSonDataClassEliminar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        // Creating List of Subject class.


        public ParseJSonDataClassEliminar(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObject != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {
                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {
            adapterCallback.onMethodCallback(mView);
            pDialogo.dismiss();
            //activarDesactivarBotones(btnLimpiar, 1);
            SweetAlertDialog sDialogo = new SweetAlertDialog(context);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

}

class ViewItemCarrito
{
    TextView txtNombre_manual;
    TextView txtPrecio_manual;
    ImageView ivImagenManual;
    Button btnEliminar;



}
