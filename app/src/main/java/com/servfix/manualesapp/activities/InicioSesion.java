package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.servfix.manualesapp.utilities.PreferenceManager;
import com.servfix.manualesapp.classes.User;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class InicioSesion extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    Button btnIniciarSesion, btnRegistrarse;
    ImageView logo;
    TextView txtLogoName, txtSesion;
    TextInputLayout txtUsuario, txtPassword;

    RequestQueue rq;
    JsonRequest jrq;
    private SharedPreferences prefs;
    ProgressBar progressBar;
    int tipo_usuario = 0;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inicio_sesion);
        rq = Volley.newRequestQueue(InicioSesion.this);

        btnIniciarSesion =(Button) findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
        logo = (ImageView) findViewById(R.id.imgLogo);
        txtLogoName = (TextView) findViewById(R.id.txtLogoName);
        txtSesion = (TextView) findViewById(R.id.txtSesion);
        txtUsuario = (TextInputLayout) findViewById(R.id.txtUsuario);
        txtPassword = (TextInputLayout) findViewById(R.id.txtPassword);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        //prefs = getApplicationContext().getSharedPreferences("MisPreferenciasOrientec", Context.MODE_PRIVATE);

        preferenceManager = new PreferenceManager(getApplicationContext());

        Boolean logeado = preferenceManager.getBoolean("logeado");
        String user = preferenceManager.getString("usuario");
        String pass = preferenceManager.getString("password");

        if(logeado){

            User usuario = new User();

            usuario.setUsuario(preferenceManager.getString("email_usuario"));
            usuario.setNombre(preferenceManager.getString("nombre_usuario"));
            usuario.setId_usuario(preferenceManager.getInt("id_usuario"));
            usuario.setPaterno(preferenceManager.getString("paterno_usuario"));
            usuario.setMaterno(preferenceManager.getString("materno_usuario"));
            usuario.setCelular(preferenceManager.getString("celular"));
            usuario.setTipo_usuario(preferenceManager.getInt("tipo_usuario"));
            usuario.setId_usuario_firebase(preferenceManager.getString("id_usuario_firebase"));
            usuario.setImagen(preferenceManager.getString("imagen"));

            GlobalVariables variablesGlobales = new GlobalVariables();
            variablesGlobales.id_usuario = usuario.getId_usuario();
            variablesGlobales.nombre_usuario = usuario.getNombre();
            variablesGlobales.usuario = usuario.getUsuario();
            variablesGlobales.paterno = usuario.getPaterno();
            variablesGlobales.materno = usuario.getMaterno();
            variablesGlobales.celular = usuario.getCelular();
            variablesGlobales.tipo_usuario = usuario.getTipo_usuario();
            variablesGlobales.id_usuario_firebase = usuario.getId_usuario_firebase();
            variablesGlobales.imagen = usuario.getImagen();

            tipo_usuario = usuario.getTipo_usuario();

            getMenuTipoUsuario(tipo_usuario);

        }


        txtUsuario.getEditText().setText(user);
        txtPassword.getEditText().setText(pass);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarUsuario() || !validarPassword()){
                    return;
                }
                iniciarSesion();
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, Registrarse.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private Boolean validarUsuario(){
        boolean valido = false;

        String val = txtUsuario.getEditText().getText().toString();

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(val);

        if(val.isEmpty()){
            txtUsuario.setError("Introduzca el email");
            valido = false;
        }else if(!matcher.matches()){
            txtUsuario.setError("Formato de correo incorrecto");
            valido = false;
        }else {
            txtUsuario.setError(null);
            valido = true;
        }

        return valido;

    }

    private Boolean validarPassword(){
        String val = txtPassword.getEditText().getText().toString();

        if(val.isEmpty()){
            txtPassword.setError("Introduzca la Contraseña");
            return false;
        }else{
            txtPassword.setError(null);
            return true;
        }

    }

    private void iniciarSesion(){
        progressBar.setVisibility(View.VISIBLE);

        //SharedPreferences.Editor editor = prefs.edit();

        //preferenceManager.putString("user", txtUsuario.getEditText().getText().toString());
        //preferenceManager.putString("pass", txtPassword.getEditText().getText().toString());

        //editor.commit();

        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "sesion.php?usuario="+txtUsuario.getEditText().getText().toString()+
                "&clave="+txtPassword.getEditText().getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(InicioSesion.this,"Email o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);

        User usuario = new User();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            usuario.setUsuario(jsonObject.optString("email_usuario"));
            usuario.setNombre(jsonObject.optString("nombre_usuario"));
            usuario.setId_usuario(parseInt(jsonObject.optString("id_usuario")));
            usuario.setPaterno(jsonObject.optString("paterno_usuario"));
            usuario.setMaterno(jsonObject.optString("materno_usuario"));
            usuario.setCelular(jsonObject.optString("celular"));
            usuario.setTipo_usuario(parseInt(jsonObject.optString("tipo_usuario")));
            usuario.setId_usuario_firebase(jsonObject.optString("id_usuario_firebase"));
            usuario.setImagen(jsonObject.optString("imagen"));

            preferenceManager.putBoolean("logeado", true);
            preferenceManager.putString("usuario", txtUsuario.getEditText().getText().toString());
            preferenceManager.putString("password", txtPassword.getEditText().getText().toString());
            preferenceManager.putInt("id_usuario", usuario.getId_usuario());
            preferenceManager.putString("nombre_usuario", usuario.getNombre());
            preferenceManager.putString("paterno",  usuario.getPaterno());
            preferenceManager.putString("materno", usuario.getMaterno());
            preferenceManager.putString("celular", usuario.getCelular());
            preferenceManager.putInt("tipo_usuario", usuario.getTipo_usuario());
            preferenceManager.putString("id_usuario_firebase", usuario.getId_usuario_firebase());
            preferenceManager.putString("imagen", usuario.getImagen());

            GlobalVariables variablesGlobales = new GlobalVariables();
            variablesGlobales.id_usuario = usuario.getId_usuario();
            variablesGlobales.nombre_usuario = usuario.getNombre();
            variablesGlobales.usuario = usuario.getUsuario();
            variablesGlobales.paterno = usuario.getPaterno();
            variablesGlobales.materno = usuario.getMaterno();
            variablesGlobales.celular = usuario.getCelular();
            variablesGlobales.tipo_usuario = usuario.getTipo_usuario();
            variablesGlobales.id_usuario_firebase = usuario.getId_usuario_firebase();
            variablesGlobales.imagen = usuario.getImagen();

            tipo_usuario = usuario.getTipo_usuario();


        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(InicioSesion.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }

        getMenuTipoUsuario(tipo_usuario);

    }

    private void getMenuTipoUsuario(int tipoUsusario){
        if(tipoUsusario == 2){
            Intent intencion = new Intent(InicioSesion.this, MenuTecnicos.class);
            intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intencion);
        }else {

            Intent intencion = new Intent(InicioSesion.this, MenuPrincipal.class);
            intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intencion);
        }
    }

    @Override public void onBackPressed() {

    }
}