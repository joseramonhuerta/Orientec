package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.ChatSoporte;
import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.DetalleManual;
import com.servfix.manualesapp.DetalleManualPdf;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.ReproductorVimeo;
import com.servfix.manualesapp.adapters.ConversacionesAdapter;
import com.servfix.manualesapp.adapters.CursosVendidosAdapter;
import com.servfix.manualesapp.adapters.InformacionUsuarioAdapter;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityInformacionUsuarioBinding;
import com.servfix.manualesapp.listeners.ConversacionesListerner;
import com.servfix.manualesapp.listeners.InformacionUsuarioListener;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InformacionUsuario extends AppCompatActivity implements InformacionUsuarioListener {
    ActivityInformacionUsuarioBinding binding;
    int id_usuario = 0;
    GlobalVariables globalVariables;
    PreferenceManager preferenceManager;
    View mView;
    String FinalJSonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformacionUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mView = binding.getRoot();


        init();
        getValuesUsuario(mView);
        setListeners();
    }

    private void init(){
        id_usuario = getIntent().getIntExtra("id_usuario", 0);
    }

    private void setListeners(){
        binding.btnAtrasInformacionUsuario.setOnClickListener(v->onBackPressed());


    }

    private void getValuesUsuario(View view){
        final View vista = view;
        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "obtenerinformacionusuario.php?id_usuario_tecnico=" + String.valueOf(id_usuario) + "&id_usuario=" + String.valueOf(id_user);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;
                            // Calling method to parse JSON object.
                            new InformacionUsuario.ParseJSonDataClass(vista).execute();
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

    @Override
    public void onInformacionUsuarioClicked(Manual manual) {


        Intent intencion = new Intent(getApplicationContext(), DetalleManual.class);
        intencion.putExtra("manual", (Serializable)manual);
        intencion.putExtra("perfilUsuario", 1);
        intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intencion);


    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        public View view;
        public String msg, nombre_usuario, conocimientos_tecnicos, imagen;
        boolean success;
        JSONArray jsonArray,jsonArrayDatos;
        List<Manual> manualList;
        // Creating List of Subject class.


        public ParseJSonDataClass(View view) {
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

                if (FinalJSonObject != null) {
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");
                        jsonObject = jsonArray.getJSONObject(0);

                        nombre_usuario = jsonObject.getString("nombre_usuario");
                        conocimientos_tecnicos = jsonObject.getString("conocimientos_tecnicos");
                        imagen = jsonObject.getString("imagen");

                        jsonArrayDatos = jsonObject.getJSONArray("manuales");


                        Manual manual;
                        GlobalVariables variablesGlobales = new GlobalVariables();
                        String URLPORTADA =  variablesGlobales.URLServicio;
                        /*
                        if(success){
                            jsonObjectDatos = jsonArray.getJSONObject(0);
                        }
                        */
                        // Defining CustomSubjectNamesList AS Array List.
                        manualList = new ArrayList<Manual>();

                        for (int i = 0; i < jsonArrayDatos.length(); i++)
                        {
                            manual = new Manual();
                            jsonObjectDatos = jsonArrayDatos.getJSONObject(i);
                            manual.setId_manual(Integer.parseInt(jsonObjectDatos.getString("id_manual")));
                            manual.setNombre_manual(jsonObjectDatos.getString("nombre_manual"));
                            manual.setDescripcion_manual(jsonObjectDatos.getString("descripcion_manual"));
                            manual.setPaginas(jsonObjectDatos.getString("paginas"));
                            manual.setPrecio(Double.parseDouble(jsonObjectDatos.getString("precio")));
                            manual.setNombre_categoria(jsonObjectDatos.getString("nombre_categoria"));
                            manual.setCalificacion(Double.parseDouble(jsonObjectDatos.getString("calificacion")));
                            manual.setNombre_tecnico(jsonObjectDatos.getString("nombre_usuario"));
                            manual.setImagen_tecnico(jsonObjectDatos.getString("imagen_usuario"));
                            manual.setId_usuario_tecnico(Integer.parseInt(jsonObjectDatos.getString("id_usuario_creador")));
                            manual.setEsgratuito(Integer.parseInt(jsonObjectDatos.getString("esgratuito")));
                            manual.setObtenido(Integer.parseInt(jsonObjectDatos.getString("obtenido")));
                            manual.setUrl_portada(GlobalVariables.URLServicio + "manuales/" + jsonObjectDatos.getString("id_manual") + "/"+ jsonObjectDatos.getString("url_portada"));
                            manual.setUrl_detalle(GlobalVariables.URLServicio + "manuales/" + jsonObjectDatos.getString("id_manual") + "/"+ jsonObjectDatos.getString("url_detalle"));
                            manualList.add(manual);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        binding.progressBarInformacionUsuario.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                binding.progressBarInformacionUsuario.setVisibility(View.GONE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            binding.progressBarInformacionUsuario.setVisibility(View.GONE);
            binding.txtNombreUsuarioInfo.setText(nombre_usuario);
            binding.txtDescripcionUsuarioInfo.setText(conocimientos_tecnicos);
            //binding.txtDescripcionUsuarioInfo.setText(jsonObject.getString("conocimientos_tecnicos"));
            binding.ivProfileInfo.setImageBitmap(getBitmapFromEncodedString(imagen));

            binding.listviewInformacionUsuario.setAdapter(new InformacionUsuarioAdapter(manualList, getApplicationContext(), InformacionUsuario.this));

            binding.listviewInformacionUsuario.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        }
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