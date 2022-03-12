package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.activities.CarritoCompras;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetalleManual extends AppCompatActivity {
    int id_curso = 0;
    Context mContext;
    StringRequest stringRequest;
    String FinalJSonObject;
    String HTTP_URL;

    View viewCursoDetalle;
    androidx.appcompat.widget.Toolbar toolbarCursoDetalle;
    ImageView btnAtrasCursoDetalle;
    TextView txtTituloCursoDetalle;
    TextView txtDescripcionCursoDetalle;
    TextView txtPrecioCursoDetalle;
    ImageView ivImagenCursoDetalle;
    Button btnAgregarCursoDetalle;
    ImageView btnCarritoCompras;

    GlobalVariables vg;
    SweetAlertDialog pDialogo;

    Manual manual=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_manual);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = getApplicationContext();
        toolbarCursoDetalle = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarCursoDetalle);
        setSupportActionBar(toolbarCursoDetalle);

        btnAtrasCursoDetalle = (ImageView) findViewById(R.id.btnAtrasCursoDetalle);
        txtTituloCursoDetalle = (TextView) findViewById(R.id.txtTituloCursoDetalle);
        txtDescripcionCursoDetalle =(TextView) findViewById(R.id.txtDescripcionCursoDetalle);
        txtPrecioCursoDetalle = (TextView) findViewById(R.id.txtPrecioCursoDetalle);
        btnAgregarCursoDetalle = (Button) findViewById(R.id.btnAgregarCursoDetalle);
        ivImagenCursoDetalle = (ImageView) findViewById(R.id.ivImagenCursoDetalle);
        btnCarritoCompras = (ImageView) findViewById(R.id.btnCarritoCompras);

        vg = new GlobalVariables();

        Intent intent = getIntent();
        manual = (Manual) intent.getExtras().getSerializable("manual");

        id_curso = manual.getId_manual();
        txtTituloCursoDetalle.setText(manual.getNombre_manual().toString());
        txtDescripcionCursoDetalle.setText(manual.getDescripcion_manual().toString());
        txtPrecioCursoDetalle.setText("$ " + String.valueOf(manual.getPrecio()));


        Picasso.get().load(manual.getPortada())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(ivImagenCursoDetalle);


        btnAtrasCursoDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAgregarCursoDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarACarritoCompras(v);
            }
        });

        btnCarritoCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(DetalleManual.this, CarritoCompras.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);


            }
        });



    }

    private void agregarACarritoCompras(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(DetalleManual.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Agregando a Carrito...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final int id_usuario = variablesGlobales.id_usuario;

        HTTP_URL =variablesGlobales.URLServicio + "agregarcarritocompras.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new DetalleManual.ParseJSonDataClassGuardar(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_manual", String.valueOf(id_curso));
                parametros.put("id_usuario", String.valueOf(id_usuario));

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);


    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        Manual manual;

        public ParseJSonDataClassGuardar(View view) {
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
                        jsonArray = jsonObject.getJSONArray("datos");

                        if(success){


                            jsonObjectDatos = jsonArray.getJSONObject(0);




                        }

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        */


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
            super.onPostExecute(result);

            pDialogo.dismiss();

            SweetAlertDialog sDialogo = new SweetAlertDialog(DetalleManual.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }


}