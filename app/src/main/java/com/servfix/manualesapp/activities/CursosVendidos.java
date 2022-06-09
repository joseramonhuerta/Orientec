package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.adapters.CursosVendidosAdapter;
import com.servfix.manualesapp.adapters.EstadoCuentaAdapter;
import com.servfix.manualesapp.classes.ItemEstadoCuenta;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityCursosVendidosBinding;
import com.servfix.manualesapp.databinding.ActivityEstadoCuentaBinding;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CursosVendidos extends BaseActivity {
    ActivityCursosVendidosBinding binding;
    View mView;
    String FinalJSonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCursosVendidosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mView = binding.getRoot();
        setListeners();
        obtenerCursosVendidos(mView);
    }

    private void setListeners(){
        binding.btnAtrasCursosVendidos.setOnClickListener(v->onBackPressed());

    }

    private void obtenerCursosVendidos(View view){
        final View vista = view;
        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "obtenercursosvendidos.php?id_usuario=" + String.valueOf(id_user);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;
                            // Calling method to parse JSON object.
                            new CursosVendidos.ParseJSonDataClass(vista).execute();
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

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        public View view;
        public String msg;
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
                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Manual manual;

                        GlobalVariables variablesGlobales = new GlobalVariables();

                        String URLPORTADA =  variablesGlobales.URLServicio;

                        // Defining CustomSubjectNamesList AS Array List.
                        manualList = new ArrayList<Manual>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            manual = new Manual();
                            jsonObject = jsonArray.getJSONObject(i);
                            manual.setId_manual(Integer.parseInt(jsonObject.getString("id_manual")));
                            manual.setNombre_manual(jsonObject.getString("nombre_manual"));
                            manual.setPrecio(Double.parseDouble(jsonObject.getString("precio")));
                            manual.setNombre_categoria(jsonObject.getString("nombre_categoria"));
                            manual.setUrl_portada(GlobalVariables.URLServicio + "manuales/" + jsonObject.getString("id_manual") + "/"+ jsonObject.getString("url_portada"));
                            manualList.add(manual);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        binding.progressBarCursosVendidos.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                binding.progressBarCursosVendidos.setVisibility(View.GONE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            binding.progressBarCursosVendidos.setVisibility(View.GONE);

            binding.listviewCursosVendidos.setAdapter(new CursosVendidosAdapter(manualList, getApplicationContext()));

            binding.listviewCursosVendidos.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        }
    }

}