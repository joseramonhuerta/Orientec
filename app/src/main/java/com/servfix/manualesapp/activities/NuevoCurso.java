package com.servfix.manualesapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.databinding.ActivityNuevoCursoBinding;
import com.servfix.manualesapp.fragments.CuadroDialogoCategorias;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevoCurso extends AppCompatActivity implements CuadroDialogoCategorias.Actualizar{
    private static final int PICK_IMAGE_REQUEST = 7373;
    int id_manual = 0;
    int id_categoria = 0;
    int tipoSelected = -1;
    Manual manual;
    ActivityNuevoCursoBinding binding;
    PreferenceManager preferenceManager;
    private GlobalVariables gv;
    private String encodedImage;
    SweetAlertDialog pDialogo = null;
    private String encodedManualPDF;
    private String fileNameManual;
    String FinalJSONObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityNuevoCursoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gv = new GlobalVariables();
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadReceiverDetails();
        setListeners();

        //listenMessages();
    }
    private void init(){
        String[] opciones_tipo = {"Manual", "Video","Asesoria"};
        ArrayAdapter adapter_tipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones_tipo);
        binding.spnTipoCurso.setAdapter(adapter_tipo);

    }
    private void loadReceiverDetails(){
        manual = (Manual) getIntent().getSerializableExtra("manual");
        id_manual = manual.getId_manual();

        if(id_manual > 0){
            binding.txtTituloCursoTecnicoNuevo.setText("Editar curso");
            binding.txtNombreCursoTecnicoNuevo.getEditText().setText(manual.getNombre_manual());
            binding.txtDescripcionCursoTecnicoNuevo.getEditText().setText(manual.getDescripcion_manual());
            //imagen portada
            id_categoria = manual.getId_categoria();
            binding.txtCategoriaCursoTecnicoNuevo.getEditText().setText(manual.getNombre_categoria());
            binding.txtDuracionCursoTecnicoNuevo.getEditText().setText(manual.getPaginas());

            tipoSelected = manual.getTipo() - 1;
            binding.spnTipoCurso.setSelection(tipoSelected);
            fileNameManual = manual.getNombre_pdf();
            Picasso.get().load(manual.getPortada())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.ivImagenCursoTecnicoNuevo);

            if(manual.getEsgratuito() == 1){
                binding.checkGratuito.setChecked(true);
                binding.txtPrecioCursoTecnicoNuevo.getEditText().setEnabled(false);
                binding.txtPrecioCursoTecnicoNuevo.getEditText().setText(null);
            }else{
                binding.txtPrecioCursoTecnicoNuevo.getEditText().setEnabled(true);
                binding.txtPrecioCursoTecnicoNuevo.getEditText().setText(String.valueOf(manual.getPrecio()));
            }

            if(tipoSelected == 0)
                binding.txtNombreArchivoManual.setText(manual.getNombre_pdf());
            if(tipoSelected == 1)
                binding.txtURLCursoTecnico.getEditText().setText(manual.getUrl());

            validarSeleccionTipoCurso(tipoSelected);

        }else{
            binding.txtTituloCursoTecnicoNuevo.setText("Nuevo curso");
        }

    }

    private void setListeners(){
        binding.btnAtrasNuevoCursoTecnico.setOnClickListener(v -> onBackPressed());

        binding.spnTipoCurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSelected = position;
                validarSeleccionTipoCurso(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnCamaraCursoTecnicoNuevo.setOnClickListener((View v)-> {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);

        });

        binding.txtCategoriaCursoTecnicoNuevo.getEditText().setOnClickListener((View v) -> {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                CuadroDialogoCategorias dialogoFragment = new CuadroDialogoCategorias(getApplicationContext(), fm, binding.getRoot(), 2);

                CuadroDialogoCategorias tPrev =  (CuadroDialogoCategorias) fm.findFragmentByTag("dialogo_categorias");

                if(tPrev!=null)
                    ft.remove(tPrev);

                //dialogoFragment.setTargetFragment(mainFrag, DIALOGO_FRAGMENT);
                dialogoFragment.show(fm, "dialogo_categorias");

        });

        binding.btnNuevaCategoria.setOnClickListener((View v)-> {

                Intent intent = new Intent(getApplicationContext(), NuevaCategoria.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

        });

        binding.btnFolderCursoTecnicoNuevo.setOnClickListener((View v) ->{
            seleccionarArchivo();
        });

        binding.btnGuardarNuevoCursoTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombreCurso() | !validarDescripcion() | !validarCategoria() | !validarDuracion() | !validarImagenPortada() | !validarPrecio() | !validarURL() | !validarManual())
                    return;

                guardar(binding.getRoot());
            }
        });

        binding.checkGratuito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.txtPrecioCursoTecnicoNuevo.getEditText().setEnabled(false);
                    binding.txtPrecioCursoTecnicoNuevo.getEditText().setText(null);
                    binding.txtPrecioCursoTecnicoNuevo.setError(null);
                }else{
                    binding.txtPrecioCursoTecnicoNuevo.getEditText().setEnabled(true);
                }

            }
        });
    }

    public void guardar(View view){
        binding.btnGuardarNuevoCursoTecnico.setEnabled(false);
        pDialogo = new SweetAlertDialog(NuevoCurso.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        GlobalVariables gv = new GlobalVariables();

        String URL = gv.URLServicio + "guardarmanual.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FinalJSONObject = response;
                        new NuevoCurso.ParseJSONDataClassGuardar(view).execute();
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
                parametros.put("id_manual", String.valueOf(id_manual));
                parametros.put("id_usuario", String.valueOf(id_user));
                parametros.put("nombre_manual", binding.txtNombreCursoTecnicoNuevo.getEditText().getText().toString());
                parametros.put("descripcion_manual", binding.txtDescripcionCursoTecnicoNuevo.getEditText().getText().toString());
                parametros.put("paginas", binding.txtDuracionCursoTecnicoNuevo.getEditText().getText().toString());
                parametros.put("precio", binding.txtPrecioCursoTecnicoNuevo.getEditText().getText().toString());
                parametros.put("tipo", String.valueOf(tipoSelected + 1));

                if(encodedImage != null){
                    parametros.put("portada", encodedImage);
                }

                parametros.put("id_categoria", String.valueOf(id_categoria));

                if(binding.checkGratuito.isChecked()){
                    parametros.put("esgratuito", "1");
                }else{
                    parametros.put("esgratuito", "0");
                }
                //0=manual, 1=video, 2=asesoria
                if(tipoSelected == 0) {
                     if(encodedManualPDF != null){
                        parametros.put("encoded_manual", encodedManualPDF);
                      }

                    parametros.put("nombre_pdf", fileNameManual);
                }

                if(tipoSelected == 1)
                    parametros.put("url", binding.txtURLCursoTecnico.getEditText().getText().toString());

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
                        jsonArray = jsonObject.getJSONArray("datos");
                        if(success){
                            jsonObjectDatos = jsonArray.getJSONObject(0);
                            id_manual = Integer.parseInt(jsonObjectDatos.getString("id_manual"));
                            binding.txtTituloCursoTecnicoNuevo.setText("Editar curso");
                        }

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

            Toast.makeText(view.getContext(),msg,Toast.LENGTH_LONG).show();

            binding.btnGuardarNuevoCursoTecnico.setEnabled(true);
        }
    }

    private void seleccionarArchivo(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        chooseFile = Intent.createChooser(chooseFile, "Seleccione el archivo");
        startActivityForResult(chooseFile, PICK_IMAGE_REQUEST);
    }

    private void validarSeleccionTipoCurso(int position){
        if(position == 0){
            binding.layoutCargaManualTecnico.setVisibility(View.VISIBLE);
            binding.layoutURLCursoTecnico.setVisibility(View.GONE);
        }else if(position == 1){
            binding.layoutURLCursoTecnico.setVisibility(View.VISIBLE);
            binding.layoutCargaManualTecnico.setVisibility(View.GONE);
        }else{
            binding.layoutCargaManualTecnico.setVisibility(View.GONE);
            binding.layoutURLCursoTecnico.setVisibility(View.GONE);
        }
    }

    public boolean validarNombreCurso(){
        boolean valido = false;
        String val = binding.txtNombreCursoTecnicoNuevo.getEditText().getText().toString();
        if(val.isEmpty()){
            binding.txtNombreCursoTecnicoNuevo.setError("Introduzca el nombre");
            valido = false;
        }else{
            binding.txtNombreCursoTecnicoNuevo.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarDescripcion(){
        boolean valido = false;
        String val = binding.txtDescripcionCursoTecnicoNuevo.getEditText().getText().toString();
        if(val.isEmpty()){
            binding.txtDescripcionCursoTecnicoNuevo.setError("Introduzca la descripción");
            valido = false;
        }else{
            binding.txtDescripcionCursoTecnicoNuevo.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarImagenPortada(){
        boolean valido = false;
        if(id_manual == 0 && encodedImage == null){
            Toast.makeText(getApplicationContext(), "Seleccione una imagen de portada", Toast.LENGTH_SHORT).show();
            valido = false;
        }else{
            valido = true;
        }
        return valido;
    }

    public boolean validarCategoria(){
        boolean valido = false;
        if(id_categoria == 0){
            binding.txtCategoriaCursoTecnicoNuevo.setError("Seleccione una categoría");
            valido = false;
        }else{
            binding.txtCategoriaCursoTecnicoNuevo.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarDuracion(){
        boolean valido = false;
        String val = binding.txtDuracionCursoTecnicoNuevo.getEditText().getText().toString();
        if(val.isEmpty()){
            binding.txtDuracionCursoTecnicoNuevo.setError("Introduzca la duración");
            valido = false;
        }else{
            binding.txtDuracionCursoTecnicoNuevo.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarPrecio(){
        boolean valido = false;
        String val = binding.txtPrecioCursoTecnicoNuevo.getEditText().getText().toString();
        if(!binding.checkGratuito.isChecked() && val.isEmpty()){
            binding.txtPrecioCursoTecnicoNuevo.setError("Introduzca el precio");
            valido = false;
        }else{
            binding.txtPrecioCursoTecnicoNuevo.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarURL(){
        boolean valido = false;
        String val = binding.txtURLCursoTecnico.getEditText().getText().toString();
        if(tipoSelected == 1 && val.isEmpty()){
            binding.txtURLCursoTecnico.setError("Introduzca la URL");
            valido = false;
        }else{
            binding.txtURLCursoTecnico.setError(null);
            valido = true;
        }
        return valido;
    }

    public boolean validarManual(){
        boolean valido = false;
        if(tipoSelected == 0 && (id_manual == 0 && encodedImage == null)){
            Toast.makeText(getApplicationContext(), "Seleccione el pdf", Toast.LENGTH_SHORT).show();
            valido = false;
        }else{
            valido = true;
        }
        return valido;
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
                            binding.ivImagenCursoTecnicoNuevo.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri path = data.getData();
            try{
                InputStream inputStream = NuevoCurso.this.getContentResolver().openInputStream(path);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                encodedManualPDF = encodeManual(pdfInBytes);
                File file = new File(path.getPath());
                fileNameManual = file.getName();
                binding.txtNombreArchivoManual.setText(fileNameManual);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String encodeManual(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    public void actualizaActividad(View view, int id_cat, String nombre_categoria) {
        this.id_categoria = id_cat;
        binding.txtCategoriaCursoTecnicoNuevo.getEditText().setText(nombre_categoria);

    }
}