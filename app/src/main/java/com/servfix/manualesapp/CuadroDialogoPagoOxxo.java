package com.servfix.manualesapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.parseInt;

public class CuadroDialogoPagoOxxo extends DialogFragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    FragmentManager fm;
    View mView;
    Context mContext;
    Dialog dialogo = null;
    double importe;
    GlobalVariables vg;
    RequestQueue rq;
    JsonRequest jrq;

    TextInputLayout txtCodigoPago;
    TextView txtBeneficiario, txtNumeroTarjeta, txtLeyendaOxxo;
    Button btnAceptar, btnEnviar;

    ProgressBar progressBar;

    public interface Actualizar {

        public void actualizaActividad();

    }
    Actualizar listener;
    Activity activity;

    public CuadroDialogoPagoOxxo(Context context, View view, double total) {
        this.mContext = context;
        this.mView = view;
        this.importe = total;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Actualizar) context;
            activity = getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cuadro_dialogo_informacion_oxxo, null);

        txtBeneficiario = (TextView) v.findViewById(R.id.txtBeneficiario);
        txtNumeroTarjeta = (TextView) v.findViewById(R.id.txtNumeroTarjeta);
        txtLeyendaOxxo = (TextView) v.findViewById(R.id.txtLeyendaOxxo);
        txtCodigoPago = (TextInputLayout) v.findViewById(R.id.txtCodigoPago);

        btnAceptar = (Button) v.findViewById(R.id.btnAceptarPagoOxxo);
        btnEnviar = (Button) v.findViewById(R.id.btnEnviarCodigo);

        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarPagoOxxo);

        //distancia = b.getDouble("distancia");
        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vg = new GlobalVariables();
        rq = Volley.newRequestQueue(v.getContext());

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCodigoPago();
            }
        });

        cargarDatosBeneficiario(v);
        return v;
    }

    private void cargarDatosBeneficiario(View view) {
        //progressBar.setVisibility(View.VISIBLE);

        String url = vg.URLServicio + "obtenerdatosbeneficiario.php";
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }

    private void validarCodigoPago(){
        progressBar.setVisibility(View.VISIBLE);

        String url = vg.URLServicio + "validarcodigopago.php?id_usuario="+String.valueOf(vg.id_usuario)+
                "&total="+String.valueOf(this.importe);

        jrq=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    boolean success =jsonObject.optBoolean("success");
                    String msg =jsonObject.optString("msg");

                    if(success){
                        dialogo.dismiss();
                        //listener.actualizaActividadUsuario(mView,id_usuario,nombre_usuario);
                        listener.actualizaActividad();
                    }

                    Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        rq.add(jrq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),error.getMessage().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        //progressBar.setVisibility(View.GONE);
        try{

            JSONObject jsonObject = new JSONObject(response.toString());
            boolean success =jsonObject.optBoolean("success");
            String msg =jsonObject.optString("msg");

            if(success){
                JSONArray jsonArrayDatos = jsonObject.getJSONArray("datos");
                JSONObject jsonObjectDatos = jsonArrayDatos.getJSONObject(0);
                txtBeneficiario.setText(jsonObjectDatos.optString("beneficiario"));
                txtNumeroTarjeta.setText(jsonObjectDatos.optString("numero_cuenta"));
                txtLeyendaOxxo.setText(jsonObjectDatos.optString("leyenda_pago_oxxo"));

            }else{
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            Toast.makeText(getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}

