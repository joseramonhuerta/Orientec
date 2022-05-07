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
import com.servfix.manualesapp.adapters.EstadoCuentaAdapter;
import com.servfix.manualesapp.adapters.ListViewAdapterManuales;
import com.servfix.manualesapp.adapters.MyRecyclerViewAdapter;
import com.servfix.manualesapp.classes.ItemEstadoCuenta;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityEstadoCuentaBinding;
import com.servfix.manualesapp.interfaces.RecyclerViewOnItemClickListener;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EstadoCuenta extends AppCompatActivity {
    ActivityEstadoCuentaBinding binding;
    View mView;
    String FinalJSonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEstadoCuentaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mView = binding.getRoot();
        setListeners();
        obtenerEstadoCuenta(mView);
    }

    private void setListeners(){
        binding.btnAtrasEstadoCuenta.setOnClickListener(v->onBackPressed());

    }

    private void obtenerEstadoCuenta(View view){
        final View vista = view;
        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "obtenerestadocuenta.php?id_usuario=" + String.valueOf(id_user);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;
                            // Calling method to parse JSON object.
                            new EstadoCuenta.ParseJSonDataClass(vista).execute();
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
        List<ItemEstadoCuenta> itemEstadoCuentaList;
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
                        ItemEstadoCuenta itemEstadoCuenta;

                        // Defining CustomSubjectNamesList AS Array List.
                        itemEstadoCuentaList = new ArrayList<ItemEstadoCuenta>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            itemEstadoCuenta = new ItemEstadoCuenta();
                            jsonObject = jsonArray.getJSONObject(i);
                            itemEstadoCuenta.Fecha = jsonObject.getString("fecha");
                            itemEstadoCuenta.Concepto = jsonObject.getString("concepto");
                            itemEstadoCuenta.Cargo = Double.parseDouble(jsonObject.getString("cargo"));
                            itemEstadoCuenta.Abono = Double.parseDouble(jsonObject.getString("abono"));
                            itemEstadoCuenta.Saldo = Double.parseDouble(jsonObject.getString("saldo"));

                            itemEstadoCuentaList.add(itemEstadoCuenta);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        binding.progressBarEstadoCuenta.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                binding.progressBarEstadoCuenta.setVisibility(View.GONE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            binding.progressBarEstadoCuenta.setVisibility(View.GONE);

            binding.listviewEstadoCuenta.setAdapter(new EstadoCuentaAdapter(itemEstadoCuentaList, getApplicationContext()));

            binding.listviewEstadoCuenta.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        }
    }


}