package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.servfix.manualesapp.activities.InformacionUsuario;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetalleManual extends AppCompatActivity {
    int id_curso = 0;
    int id_usuario = 0;
    int id_usuario_tecnico = 0;
    Context mContext;
    StringRequest stringRequest;
    String FinalJSonObject, FinalJSonObjectDetalle, FinalJSonObjectUsuario;
    String HTTP_URL;

    View mView;
    androidx.appcompat.widget.Toolbar toolbarCursoDetalle;
    ImageView btnAtrasCursoDetalle;
    TextView txtTituloCursoDetalle;
    TextView txtDescripcionCursoDetalle;
    TextView txtPrecioCursoDetalle;
    TextView txtGratuitoCursoDetalle;
    TextView txtCalificacion;
    RatingBar rtCalificacion;
    ImageView ivImagenCursoDetalle;
    Button btnAgregarCursoDetalle;
    ImageView btnCarritoCompras;
    TextView txtNombreUsuario;
    ImageView ivImagenUsuario;
    LinearLayout layInfoUsuarioCurso;
    ImageView ivNuevoCursoDetalle;

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
        mView = (View) findViewById(R.id.viewCursoDetalle);
        btnAtrasCursoDetalle = (ImageView) findViewById(R.id.btnAtrasCursoDetalle);
        txtTituloCursoDetalle = (TextView) findViewById(R.id.txtTituloCursoDetalle);
        txtDescripcionCursoDetalle =(TextView) findViewById(R.id.txtDescripcionCursoDetalle);
        txtPrecioCursoDetalle = (TextView) findViewById(R.id.txtPrecioCursoDetalle);
        btnAgregarCursoDetalle = (Button) findViewById(R.id.btnAgregarCursoDetalle);
        ivImagenCursoDetalle = (ImageView) findViewById(R.id.ivImagenCursoDetalle);
        btnCarritoCompras = (ImageView) findViewById(R.id.btnCarritoCompras);
        txtCalificacion = (TextView) findViewById(R.id.txtCalificacionCursoDetalle);
        rtCalificacion = (RatingBar) findViewById(R.id.rtCalificacionCursoDetalle);
        ivImagenUsuario = (ImageView) findViewById(R.id.ivProfileCurso);
        txtNombreUsuario = (TextView) findViewById(R.id.txtNombreUsuarioCurso);
        layInfoUsuarioCurso = (LinearLayout) findViewById(R.id.layInfoUsuarioCurso);
        txtGratuitoCursoDetalle = (TextView) findViewById(R.id.txtGratuitoCursoDetalle);
        ivNuevoCursoDetalle = (ImageView) findViewById(R.id.ivNuevoCursoDetalle);

        vg = new GlobalVariables();
        id_usuario = vg.id_usuario;
        Intent intent = getIntent();
        manual = (Manual) intent.getExtras().getSerializable("manual");
        int perfilUsuario = intent.getExtras().getInt("perfilUsuario");
        id_usuario_tecnico = manual.getId_usuario_tecnico();
        id_curso = manual.getId_manual();
        txtTituloCursoDetalle.setText(manual.getNombre_manual());
        txtDescripcionCursoDetalle.setText(manual.getDescripcion_manual());

        if(manual.getEsgratuito() == 1){
            txtPrecioCursoDetalle.setVisibility(View.GONE);
            txtGratuitoCursoDetalle.setVisibility(View.VISIBLE);
        }else {
            txtPrecioCursoDetalle.setText("$ " + getPrecioFormatoMoneda(manual.getPrecio()));
        }

        if(manual.getEsnuevo() == 1){
            ivNuevoCursoDetalle.setVisibility(View.VISIBLE);
        }else {
            ivNuevoCursoDetalle.setVisibility(View.GONE);
        }

        txtCalificacion.setText(String.valueOf(getPrecioFormatoMoneda(manual.getCalificacion())));
        rtCalificacion.setRating((float) manual.getCalificacion());
        txtNombreUsuario.setText(manual.getNombre_tecnico());
        ivImagenUsuario.setImageBitmap(getBitmapFromEncodedString(manual.getImagen_tecnico()));

        if(perfilUsuario == 1){
            layInfoUsuarioCurso.setVisibility(View.GONE);
        }

        if(manual.getEsgratuito() == 1)
            btnAgregarCursoDetalle.setText("Obtener ahora");
        else
            btnAgregarCursoDetalle.setText("Agregar a Carrito");

        if(manual.getObtenido() > 0){
            btnAgregarCursoDetalle.setVisibility(View.GONE);
        }

        Picasso.get().load(manual.getUrl_detalle())
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(ivImagenCursoDetalle);

        //ivImagenCursoDetalle.setImageBitmap(getBitmapFromEncodedString(manual.getImagen_detalle()));
        //getImagenDetalle(mView, id_curso);
        getImagenUsuario(mView, id_usuario_tecnico);
        btnAtrasCursoDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAgregarCursoDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manual.getEsgratuito() == 1){
                    pagarCarrito(mView, "", "");
                }else {
                    agregarACarritoCompras(v);
                }
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

        layInfoUsuarioCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mContext, InformacionUsuario.class);
                intencion.putExtra("id_usuario", manual.getId_usuario_tecnico());
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

    }

    private void getImagenDetalle(View vista, int id_manual){
        GlobalVariables variablesGlobales = new GlobalVariables();
        final int id_usuario = variablesGlobales.id_usuario;

        HTTP_URL =variablesGlobales.URLServicio + "obtenerimagendetalle.php?";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest sq = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObjectDetalle = response ;

                // Calling method to parse JSON object.
                new DetalleManual.ParseJSonDataClassImagenDetalle(vista).execute();
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
                parametros.put("id_manual", String.valueOf(id_manual));
                return parametros;
            }
        };

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(sq);
    }

    private void getImagenUsuario(View vista, int id_user_tecnico){
        GlobalVariables variablesGlobales = new GlobalVariables();
        final int id_usuario = variablesGlobales.id_usuario;

        HTTP_URL =variablesGlobales.URLServicio + "obtenerimagenusuario.php?";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest sq = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObjectUsuario = response ;

                // Calling method to parse JSON object.
                new DetalleManual.ParseJSonDataClassImagenUsuario(vista).execute();
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
                parametros.put("id_usuario", String.valueOf(id_user_tecnico));
                return parametros;
            }
        };

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(sq);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }else{
            return null;
        }
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
            Toast.makeText(context, this.msg, Toast.LENGTH_LONG).show();
            finish();

        }
    }

    public void pagarCarrito(View view, String referencia, String id_openpay){
        final View vista = view;
        pDialogo = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Procesando, espere...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "pagarcarrito.php?id_carrito_compras=" + String.valueOf(0) + "&id_openpay=" + id_openpay + "&referencia_openpay=" + referencia + "&formapago=" + String.valueOf(99) + "&id_manual=" + String.valueOf(id_curso) + "&id_usuario=" + String.valueOf(id_usuario);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;

                            // Calling method to parse JSON object.
                            new DetalleManual.ParseJSonDataClassPagarCarrito(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

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

    private class ParseJSonDataClassPagarCarrito extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        // Creating List of Subject class.


        public ParseJSonDataClassPagarCarrito(View view) {
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

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });*/


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
            //loadCarrito(view);
            pDialogo.dismiss();
            Toast.makeText(context, this.msg, Toast.LENGTH_LONG).show();
            finish();

        }
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }

    private class ParseJSonDataClassImagenDetalle extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public String imagen_detalle;
        // Creating List of Subject class.


        public ParseJSonDataClassImagenDetalle(View view) {
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
                if (FinalJSonObjectDetalle != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectDetalle);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        jsonObject = jsonArray.getJSONObject(0);
                        imagen_detalle = jsonObject.getString("imagen_detalle");

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
            ivImagenCursoDetalle.setImageBitmap(getBitmapFromEncodedString(imagen_detalle));

        }
    }


    private class ParseJSonDataClassImagenUsuario extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public String imagen_usuario;
        // Creating List of Subject class.


        public ParseJSonDataClassImagenUsuario(View view) {
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
                if (FinalJSonObjectUsuario != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectUsuario);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        jsonObject = jsonArray.getJSONObject(0);
                        imagen_usuario = jsonObject.getString("imagen_usuario");

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
            ivImagenUsuario.setImageBitmap(getBitmapFromEncodedString(imagen_usuario));

        }
    }


}