package com.servfix.manualesapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.activities.CuentaBancaria;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CuadroDialogoRankin extends DialogFragment {
    int id_usuario_manual;
    int id_manual;
    RatingBar ratingBar;
    Button btnGuardarRatin;
    Button btnSalirRatin;
    ProgressBar progressBar;
    View mView;
    String FinalJSONObject;
    Context mContext;
    Dialog dialogo = null;

    public CuadroDialogoRankin(Context context, int id_usuario_manual,  int id_manual) {
        this.mContext = context;
        this.id_usuario_manual = id_usuario_manual;
        this.id_manual = id_manual;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cuadro_dialogo_rankin, null);
        ratingBar = (RatingBar) v.findViewById(R.id.rtCalificacion);
        btnGuardarRatin = (Button) v.findViewById(R.id.btnGuardarRatin);
        btnSalirRatin = (Button) v.findViewById(R.id.btnSalirRatin);
        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarRatin);
        mView = v;

        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnGuardarRatin.setOnClickListener(v1 -> {
            if(ratingBar.getRating() > 0)
                guardarCalificacion(mView);
        });

        btnSalirRatin.setOnClickListener(v1 -> {
           dialogo.dismiss();
        });


        return v;
    }

    private void guardarCalificacion(View view){
        progressBar.setVisibility(View.VISIBLE);

        GlobalVariables gv = new GlobalVariables();

        String URL = gv.URLServicio + "calificarmanual.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FinalJSONObject = response;
                        new CuadroDialogoRankin.ParseJSONDataClassGuardar(view).execute();
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
                GlobalVariables gv = new GlobalVariables();
                int id_user = gv.id_usuario;

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario_manual", String.valueOf(id_usuario_manual));
                parametros.put("id_manual", String.valueOf(id_manual));
                parametros.put("id_usuario",  String.valueOf(id_user));
                parametros.put("calificacion", String.valueOf(ratingBar.getRating()));
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
            progressBar.setVisibility(View.GONE);
            if(success){
                dialogo.dismiss();
            }
            Toast.makeText(view.getContext(),msg,Toast.LENGTH_LONG).show();
        }
    }
}
