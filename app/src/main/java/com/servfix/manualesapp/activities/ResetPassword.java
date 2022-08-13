package com.servfix.manualesapp.activities;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.Registrarse;
import com.servfix.manualesapp.classes.User;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    TextView txtAtrasResetPassword;
    Button btnEnviarEmail;
    TextInputLayout txtEmailReset;

    RequestQueue rq;
    JsonRequest jrq;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_reset_password);
        rq = Volley.newRequestQueue(ResetPassword.this);

        txtEmailReset = (TextInputLayout) findViewById(R.id.txtEmailReset);
        btnEnviarEmail = (Button) findViewById(R.id.btnEnviarEmail);
        txtAtrasResetPassword = (TextView) findViewById(R.id.txtAtrasResetPassword);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarReset);

        txtAtrasResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, InicioSesion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        btnEnviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarEmail()){
                    enviarPassword();
                }
            }
        });

    }

    private void enviarPassword(){
        progressBar.setVisibility(View.VISIBLE);
        Log.i("enviarPassword","Enviar Password");
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "sendmailresetpassword.php?email="+txtEmailReset.getEditText().getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }

    private Boolean validarEmail(){
        boolean valido = false;

        String val = txtEmailReset.getEditText().getText().toString();

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(val);

        if(val.isEmpty()){
            txtEmailReset.setError("Introduzca el email");
            valido = false;
        }else if(!matcher.matches()){
            txtEmailReset.setError("Formato de correo incorrecto");
            valido = false;
        }else {
            txtEmailReset.setError(null);
            valido = true;
        }

        return valido;

    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(ResetPassword.this,"Error en la petición.", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        String msg ="";

        JSONObject jsonObject = null;

        try {
           msg = response.getString("msg");
           Toast.makeText(ResetPassword.this,msg, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {

            Toast.makeText(ResetPassword.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }



    }

    @Override public void onBackPressed() {

    }
}