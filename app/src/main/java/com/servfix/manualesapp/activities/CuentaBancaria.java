package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.databinding.ActivityCuentaBancariaBinding;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CuentaBancaria extends BaseActivity {
    int id_usuario = 0;
    ActivityCuentaBancariaBinding binding;
    PreferenceManager preferenceManager;
    GlobalVariables gv;
    View mView;
    String FinalJSONObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCuentaBancariaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mView = binding.getRoot();
        preferenceManager = new PreferenceManager(getApplicationContext());
        gv = new GlobalVariables();
        init();
        setListeners();
        //load()

    }

    private void init(){
        id_usuario = gv.id_usuario;
        binding.txtBeneficiarioCuentaBancaria.getEditText().setText(preferenceManager.getString(Constants.KEY_BENEFICIARIO));
        binding.txtNumeroTarjetaCuentaBancaria.getEditText().setText(String.valueOf(preferenceManager.getString(Constants.KEY_CUENTA_BANCARIA)));
        binding.progressBarCuentaBancaria.setVisibility(View.GONE);
    }

    private void setListeners(){
        binding.btnAtrasCuentaBancaria.setOnClickListener(v-> onBackPressed());

        binding.btnGuardarCuentaBancaria.setOnClickListener((View v)->{
            guardarCuenta(mView);
        });
    }

    public void guardarCuenta(View view){
        binding.progressBarCuentaBancaria.setVisibility(View.VISIBLE);

        GlobalVariables gv = new GlobalVariables();

        String URL = gv.URLServicio + "guardarcuentabancaria.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FinalJSONObject = response;
                        new CuentaBancaria.ParseJSONDataClassGuardar(view).execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", String.valueOf(id_usuario));
                parametros.put("beneficiario", binding.txtBeneficiarioCuentaBancaria.getEditText().getText().toString());
                parametros.put("cuenta_bancaria", binding.txtBeneficiarioCuentaBancaria.getEditText().getText().toString());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);

    }

    private class ParseJSONDataClassGuardar extends AsyncTask<Void, Void, Void> {
        public Context context;
        public View view;
        public String msg;
        public boolean success;

        public ParseJSONDataClassGuardar(View view){
            this.view = view;
            this.context = view.getContext();
        }

        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0){
            try{
                if(FinalJSONObject != null){
                    JSONArray jsonArray = null;
                    JSONObject jsonObject, jsonObjectDatos;
                    try{
                        jsonObject = new JSONObject(FinalJSONObject);
                        success = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            binding.progressBarCuentaBancaria.setVisibility(View.GONE);
            if(success){
                preferenceManager.putString(Constants.KEY_BENEFICIARIO, binding.txtBeneficiarioCuentaBancaria.getEditText().getText().toString());
                preferenceManager.putString(Constants.KEY_CUENTA_BANCARIA, binding.txtNumeroTarjetaCuentaBancaria.getEditText().getText().toString());
            }
            Toast.makeText(view.getContext(),msg,Toast.LENGTH_LONG).show();
        }
    }

}