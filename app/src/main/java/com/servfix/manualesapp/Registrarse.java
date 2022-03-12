package com.servfix.manualesapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servfix.manualesapp.activities.InicioSesion;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {
    TextInputLayout txtNombre, txtUsuario,txtPassword, txtPaterno, txtMaterno, txtCelular, txtCodigoRegistro;
    CheckBox chkTecnico;
    Button btnRegistro, btnBackLogin;
    ImageView logo,ivProfile;
    TextView txtLogoName, txtAgregarImagen;
    ProgressBar progressBar;
    String encodedImage = "";
    FrameLayout layoutImage;

    RequestQueue rq;
    JsonRequest jrq;

    String FinalJSonObject;
    StringRequest stringRequest;

    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registrarse);
        mView = (View) findViewById(R.id.viewRegistro);
        rq = Volley.newRequestQueue(Registrarse.this);
        progressBar =(ProgressBar) findViewById(R.id.ProgressBarRegistro);
        txtNombre = (TextInputLayout) findViewById(R.id.txtNombreRegistro);
        txtUsuario = (TextInputLayout) findViewById(R.id.txtUsuarioRegistro);
        txtPaterno = (TextInputLayout) findViewById(R.id.txtPaternoRegistro);
        txtMaterno = (TextInputLayout) findViewById(R.id.txtMaternoRegistro);
        txtCodigoRegistro = (TextInputLayout) findViewById(R.id.txtCodigoRegistro);
        txtCelular = (TextInputLayout) findViewById(R.id.txtCelularRegistro);
        txtPassword = (TextInputLayout) findViewById(R.id.txtPasswordRegistro);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        btnBackLogin = (Button) findViewById(R.id.btnBackLogin);
        logo = (ImageView) findViewById(R.id.imgLogoRegistro);
        ivProfile = (ImageView) findViewById(R.id.ivProfileRegistro);
        txtLogoName = (TextView) findViewById(R.id.txtLogoNameRegistro);
        txtAgregarImagen = (TextView) findViewById(R.id.layAgregarImagen);
        layoutImage = (FrameLayout) findViewById(R.id.layoutImage);
        chkTecnico = (CheckBox) findViewById(R.id.chkTecnico);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarUsuario() | !validaNombre() | !validaPassword() | !validaPaterno() | !validaMaterno() | !validaCelular() | !validaImagen()  | !validaCodigoRegistro())
                    return;

                registrar(mView);
            }
        });

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registrarse.this, InicioSesion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        chkTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkTecnico.isChecked()){
                    txtCodigoRegistro.setVisibility(View.VISIBLE);
                }else{
                    txtCodigoRegistro.setVisibility(View.GONE);
                    txtCodigoRegistro.setError(null);
                }
            }
        });

        layoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });


    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ivProfile.setImageBitmap(bitmap);
                            txtAgregarImagen.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

    );

    private boolean validaPassword() {
        boolean valido = false;

        String val = txtPassword.getEditText().getText().toString();

        if(val.isEmpty()){
            txtPassword.setError("Introduzca el password");
            valido = false;
        }else{
            txtPassword.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaNombre(){
        boolean valido = false;

        String val = txtNombre.getEditText().getText().toString();

        if(val.isEmpty()){
            txtNombre.setError("Introduzca el nombre del usuario");
            valido = false;
        }else{
            txtNombre.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaCodigoRegistro(){
        boolean valido = false;

        String val = txtCodigoRegistro.getEditText().getText().toString();

        if(val.isEmpty() && chkTecnico.isChecked()){
            txtCodigoRegistro.setError("Introduzca el codigo de registro");
            valido = false;
        }else{
            txtCodigoRegistro.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaImagen(){
        boolean valido = false;

        String val = encodedImage;

        if(val.isEmpty()){
              valido = false;
        }else{
            valido = true;
        }

        return valido;
    }

    private boolean validaPaterno(){
        boolean valido = false;

        String val = txtPaterno.getEditText().getText().toString();

        if(val.isEmpty()){
            txtPaterno.setError("Introduzca el apellido paterno");
            valido = false;
        }else{
            txtPaterno.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaMaterno(){
        boolean valido = false;

        String val = txtMaterno.getEditText().getText().toString();

        if(val.isEmpty()){
            txtMaterno.setError("Introduzca el apellido materno");
            valido = false;
        }else{
            txtMaterno.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaCelular(){
        boolean valido = false;

        String val = txtCelular.getEditText().getText().toString();

        if(val.isEmpty()){
            txtCelular.setError("Introduzca el número de celular");
            valido = false;
        }else{
            txtCelular.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarUsuario() {
        boolean valido = false;

        String val = txtUsuario.getEditText().getText().toString();

        String expression = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";
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

    private void registrar(View vista){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables gv = new GlobalVariables();

        String HTTP_URL =gv.URLServicio + "registrar_usuario.php?";
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new Registrarse.ParseJSonDataClassGuardar(vista).execute();
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
                String tipo = "";
                String nombre = txtNombre.getEditText().getText().toString();
                String usuario = txtUsuario.getEditText().getText().toString();
                String pass = txtPassword.getEditText().getText().toString();
                String paterno = txtPaterno.getEditText().getText().toString();
                String materno = txtMaterno.getEditText().getText().toString();
                String celular = txtCelular.getEditText().getText().toString();
                String codigo_registro = txtCodigoRegistro.getEditText().getText().toString();
                String imagen = null;

                if(chkTecnico.isChecked())
                    tipo = "2";
                else
                    tipo = "1";

                Map<String, String> parametros = new HashMap<>();

                parametros.put("nombre", nombre);
                parametros.put("usuario", usuario);
                parametros.put("password", pass);
                parametros.put("paterno", paterno);
                parametros.put("materno", materno);
                parametros.put("celular", celular);
                parametros.put("tipo_usuario", tipo);
                parametros.put("codigo_registro", codigo_registro);


                if(encodedImage != null){
                    parametros.put("imagen", encodedImage);
                }

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public boolean success;
        public JSONArray jsonArrayDatos = null;

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
                    JSONObject jsonObject;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        this.success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                        this.jsonArrayDatos = jsonObject.getJSONArray("datos");

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
            progressBar.setVisibility(View.GONE);

            if(this.success){
                JSONObject jsonObjectDatos;

                try {
                    jsonObjectDatos = jsonArrayDatos.getJSONObject(0);
                    int id = jsonObjectDatos.getInt("id_usuario");
                    String nombre = jsonObjectDatos.getString("nombre_usuario") + " " + jsonObjectDatos.getString("paterno_usuario") + " " + jsonObjectDatos.getString("materno_usuario");
                    String email_usuario = jsonObjectDatos.getString("email_usuario");

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("id_usuario", String.valueOf(id));
                    user.put("nombre_usuario", nombre);
                    user.put("email_usuario", email_usuario);
                    user.put("imagen", encodedImage);
                    database.collection("users")
                            .add(user)
                            .addOnSuccessListener(documentReference -> {
                            GlobalVariables gv = new GlobalVariables();
                            gv.id_usuario_firebase = documentReference.getId();
                            actualizarIDFirebase(id, documentReference.getId());
                            })
                            .addOnFailureListener(exception ->{

                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(Registrarse.this, InicioSesion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }else{
                Toast.makeText(Registrarse.this, this.msg, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onBackPressed() {

    }

    public void actualizarIDFirebase(int id_usuario, String id_usuario_firebase){
        GlobalVariables gv = new GlobalVariables();
        String HTTP_URL =gv.URLServicio + "actualizar_usuario_firebase.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HTTP_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // En caso de tener algun error en la obtencion de los datos

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new Hashtable<String, String>();
                parametros.put("id_usuario", String.valueOf(id_usuario));
                parametros.put("id_usuario_firebase", id_usuario_firebase.toString());

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }
}