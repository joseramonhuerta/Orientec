package com.servfix.manualesapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.servfix.manualesapp.databinding.ActivityNuevaCategoriaBinding;
import com.servfix.manualesapp.databinding.ActivityNuevoCursoBinding;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevaCategoria extends AppCompatActivity {
    ActivityNuevaCategoriaBinding binding;
    String encodedImage = null;
    SweetAlertDialog pDialogo = null;
    String FinalJSONObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityNuevaCategoriaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();

    }

    private void init(){

    }

    private void setListeners(){
        binding.btnAtrasNuevaCategoria.setOnClickListener(v -> onBackPressed());

        binding.btnGuardarNuevaCategoria.setOnClickListener((View v) ->{
            if(!validarNombreCategoria() | !validarImagenCategoria())
                return;

            guardar(binding.getRoot());
        });

        binding.btnCamaraNuevaCategoria.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }

    public boolean validarNombreCategoria(){
        boolean valido = false;

        String val = binding.txtNombreCategoriaNueva.getEditText().getText().toString();

        if(val.isEmpty()){
            binding.txtNombreCategoriaNueva.setError("Introduzca el nombre");
            valido = false;
        }else{
            binding.txtNombreCategoriaNueva.setError(null);
            valido = true;
        }

        return valido;
    }

    public boolean validarImagenCategoria(){
        boolean valido = false;

        if(encodedImage == null){
            Toast.makeText(getApplicationContext(), "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            valido = false;
        }else{
            valido = true;
        }

        return valido;
    }

    public void guardar(View view){
        pDialogo = new SweetAlertDialog(NuevaCategoria.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        GlobalVariables gv = new GlobalVariables();

        String URL = gv.URLServicio + "guardarcategoria.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FinalJSONObject = response;
                        new NuevaCategoria.ParseJSONDataClassGuardar(view).execute();
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
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre_categoria", binding.txtNombreCategoriaNueva.getEditText().getText().toString());
                parametros.put("imagen_categoria", encodedImage);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);

    }

    private class ParseJSONDataClassGuardar extends AsyncTask<Void, Void, Void>{
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
            pDialogo.dismiss();
            if(success){
                setDefaultValues();
            }
            Toast.makeText(view.getContext(),msg,Toast.LENGTH_LONG).show();
        }
    }

    public void setDefaultValues(){
        binding.txtNombreCategoriaNueva.getEditText().setText(null);
        binding.ivImagenCategoriaNueva.setImageBitmap(null);
        encodedImage = null;
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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
                            binding.ivImagenCategoriaNueva.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

    );
}