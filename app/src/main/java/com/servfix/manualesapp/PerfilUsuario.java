package com.servfix.manualesapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servfix.manualesapp.activities.InicioSesion;
import com.servfix.manualesapp.classes.User;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.databinding.ActivityPerfilUsuarioBinding;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class PerfilUsuario extends AppCompatActivity {
    private ActivityPerfilUsuarioBinding binding;
    TabHost.TabSpec ts1,ts2;
    int id_usuario;
    String id_usuario_firebase;
    GlobalVariables variablesGlobales;
    View mView;
    String encodedImage;
    String FinalJSonObject;
    StringRequest stringRequest;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
       
        binding.tabsPerfilUsuario.setup();

        variablesGlobales = new GlobalVariables();

        id_usuario = variablesGlobales.id_usuario;
        id_usuario_firebase = preferenceManager.getString(Constants.KEY_ID_USUARIO_FIREBASE);

        ts1 = binding.tabsPerfilUsuario.newTabSpec("tabInfo");
        Resources res = getResources();
        //Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.info, null);
        ts1.setIndicator("Información", null);
        ts1.setContent(R.id.tabInformacion);
        binding.tabsPerfilUsuario.addTab(ts1);

        ts2 = binding.tabsPerfilUsuario.newTabSpec("tabTecnico");
        //drawable = ResourcesCompat.getDrawable(res, R.drawable.manual, null);
        ts2.setIndicator("Perfil técnico", null);
        ts2.setContent(R.id.tabPerfilTecnico);
        binding.tabsPerfilUsuario.addTab(ts2);
        
        setListeners();
        
        setPerfilUsuario();
    }

    private void setPerfilUsuario() {
        if(preferenceManager.getString(Constants.KEY_IMAGEN).length() > 0) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGEN), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.ivEditarPerfil.setImageBitmap(bitmap);
            encodedImage = preferenceManager.getString(Constants.KEY_IMAGEN);
        }

        binding.txtNombreUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_NOMBRE_USUARIO));
        binding.txtPaternoUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_PATERNO_USUARIO));
        binding.txtMaternoUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_MATERNO_USUARIO));
        binding.txtCelularUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_CELULAR));
        binding.txtEmailUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_USUARIO));
        binding.txtPasswordUsuarioPerfil.getEditText().setText(preferenceManager.getString(Constants.KEY_PASSWORD));
        binding.txtConocimientosTecnicos.getEditText().setText(preferenceManager.getString(Constants.KEY_CONOCIMIENTOS_TECNICOS));

        binding.progressBarEditarPerfil.setVisibility(View.GONE);
    }

    private void setListeners(){
        binding.btnAtrasEditarPerfil.setOnClickListener(v->onBackPressed());

        binding.btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        binding.btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarUsuario() | !validaNombre() | !validaPassword() | !validaPaterno() | !validaMaterno() | !validaCelular() | !validaImagen())
                    return;

               guardarPerfil(binding.viewEditarUsuario);
            }


        });
        
    }

    private boolean validaPassword() {
        boolean valido = false;

        String val = binding.txtPasswordUsuarioPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtPasswordUsuarioPerfil.setError("Introduzca el password");
            valido = false;
        }else{
            binding.txtPasswordUsuarioPerfil.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaNombre(){
        boolean valido = false;

        String val = binding.txtNombreUsuarioPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtNombreUsuarioPerfil.setError("Introduzca el nombre del usuario");
            valido = false;
        }else{
            binding.txtNombreUsuarioPerfil.setError(null);
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
        String val = binding.txtPaternoUsuarioPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtPaternoUsuarioPerfil.setError("Introduzca el apellido paterno");
            valido = false;
        }else{
            binding.txtPaternoUsuarioPerfil.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaMaterno(){
        boolean valido = false;

        String val = binding.txtMaternoUsuarioPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtMaternoUsuarioPerfil.setError("Introduzca el apellido materno");
            valido = false;
        }else{
            binding.txtMaternoUsuarioPerfil.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validaCelular(){
        boolean valido = false;

        String val = binding.txtCelularUsuarioPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtCelularUsuarioPerfil.setError("Introduzca el número de celular");
            valido = false;
        }else{
            binding.txtCelularUsuarioPerfil.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarUsuario() {
        boolean valido = false;

        String val = binding.txtEmailUsuarioPerfil.getEditText().getText().toString();

        String expression = "[a-z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(val);

        if(val.isEmpty()){
            binding.txtEmailUsuarioPerfil.setError("Introduzca el email");
            valido = false;
        }else if(!matcher.matches()){
            binding.txtEmailUsuarioPerfil.setError("Formato de correo incorrecto");
            valido = false;
        }else {
            binding.txtEmailUsuarioPerfil.setError(null);
            valido = true;
        }

        return valido;
    }

    private void guardarPerfil(View vista) {
        binding.progressBarEditarPerfil.setVisibility(View.VISIBLE);
        GlobalVariables gv = new GlobalVariables();

        String HTTP_URL =gv.URLServicio + "editar_usuario.php?";
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new PerfilUsuario.ParseJSonDataClassGuardar(vista).execute();
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
                String nombre = binding.txtNombreUsuarioPerfil.getEditText().getText().toString();
                String usuario = binding.txtEmailUsuarioPerfil.getEditText().getText().toString();
                String pass = binding.txtPasswordUsuarioPerfil.getEditText().getText().toString();
                String paterno = binding.txtPaternoUsuarioPerfil.getEditText().getText().toString();
                String materno = binding.txtMaternoUsuarioPerfil.getEditText().getText().toString();
                String celular = binding.txtCelularUsuarioPerfil.getEditText().getText().toString();
                String conocimientos = binding.txtConocimientosTecnicos.getEditText().getText().toString();

                String imagen = null;
                tipo = "1";

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", String.valueOf(id_usuario));
                parametros.put("nombre", nombre);
                parametros.put("usuario", usuario);
                parametros.put("password", pass);
                parametros.put("paterno", paterno);
                parametros.put("materno", materno);
                parametros.put("celular", celular);
                parametros.put("conocimientos_tecnicos", conocimientos);
                parametros.put("tipo_usuario", tipo);

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
            binding.progressBarEditarPerfil.setVisibility(View.GONE);

            if(this.success){
                JSONObject jsonObjectDatos;
                User usuario = new User();
                try {
                    jsonObjectDatos = jsonArrayDatos.getJSONObject(0);
                    int id = jsonObjectDatos.getInt("id_usuario");
                    String nombre = jsonObjectDatos.getString("nombre_usuario");
                    String paterno = jsonObjectDatos.getString("paterno_usuario");
                    String materno = jsonObjectDatos.getString("materno_usuario");
                    String email_usuario = jsonObjectDatos.getString("email_usuario");
                    String nombre_completo = nombre + " " + paterno + " " + materno;

                    usuario.setUsuario(jsonObjectDatos.optString("email_usuario"));
                    usuario.setNombre(jsonObjectDatos.optString("nombre_usuario"));
                    usuario.setId_usuario(parseInt(jsonObjectDatos.optString("id_usuario")));
                    usuario.setPaterno(jsonObjectDatos.optString("paterno_usuario"));
                    usuario.setMaterno(jsonObjectDatos.optString("materno_usuario"));
                    usuario.setCelular(jsonObjectDatos.optString("celular"));
                    usuario.setTipo_usuario(parseInt(jsonObjectDatos.optString("tipo_usuario")));
                    usuario.setId_usuario_firebase(jsonObjectDatos.optString("id_usuario_firebase"));
                    usuario.setImagen(jsonObjectDatos.optString("imagen"));
                    usuario.setConocimientos_tecnicos(jsonObjectDatos.optString("conocimientos_tecnicos"));
                    usuario.setPassword(jsonObjectDatos.optString("password"));

                    preferenceManager.clear();
                    preferenceManager.putBoolean(Constants.KEY_LOGEADO, true);
                    preferenceManager.putString(Constants.KEY_USUARIO, usuario.getUsuario());
                    preferenceManager.putString(Constants.KEY_PASSWORD, usuario.getPassword());
                    preferenceManager.putInt(Constants.KEY_ID_USUARIO, id_usuario);
                    preferenceManager.putString(Constants.KEY_NOMBRE_USUARIO, usuario.getNombre());
                    preferenceManager.putString(Constants.KEY_PATERNO_USUARIO,  usuario.getPaterno());
                    preferenceManager.putString(Constants.KEY_MATERNO_USUARIO, usuario.getMaterno());
                    preferenceManager.putString(Constants.KEY_NOMBRE_USUARIO_ENVIA, usuario.getNombre() + " " + usuario.getPaterno() + " " + usuario.getMaterno());
                    preferenceManager.putString(Constants.KEY_EMAIL_USUARIO, usuario.getUsuario());
                    preferenceManager.putString(Constants.KEY_CELULAR, usuario.getCelular());
                    preferenceManager.putInt(Constants.KEY_TIPO_USUARIO, usuario.getTipo_usuario());
                    preferenceManager.putString(Constants.KEY_ID_USUARIO_FIREBASE, usuario.getId_usuario_firebase());
                    preferenceManager.putString(Constants.KEY_IMAGEN, usuario.getImagen());
                    preferenceManager.putString(Constants.KEY_CONOCIMIENTOS_TECNICOS, usuario.getConocimientos_tecnicos());

                    variablesGlobales.id_usuario = usuario.getId_usuario();
                    variablesGlobales.nombre_usuario = usuario.getNombre();
                    variablesGlobales.usuario = usuario.getUsuario();
                    variablesGlobales.paterno = usuario.getPaterno();
                    variablesGlobales.materno = usuario.getMaterno();
                    variablesGlobales.celular = usuario.getCelular();
                    variablesGlobales.tipo_usuario = usuario.getTipo_usuario();
                    variablesGlobales.id_usuario_firebase = usuario.getId_usuario_firebase();
                    variablesGlobales.imagen = usuario.getImagen();
                    variablesGlobales.conocimientos_tecnicos = usuario.getConocimientos_tecnicos();

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("nombre_usuario", nombre_completo);
                    user.put("email_usuario", email_usuario);
                    user.put("imagen", encodedImage);

                    DocumentReference documentReference =
                            database.collection(Constants.KEY_USERS).document(id_usuario_firebase);
                    documentReference.update(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            Toast.makeText(PerfilUsuario.this, this.msg, Toast.LENGTH_SHORT).show();
        }
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
                            binding.ivEditarPerfil.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

    );


}