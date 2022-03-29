package com.servfix.manualesapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.databinding.ActivityNuevoCursoBinding;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NuevoCurso extends AppCompatActivity {
    int id_manual = 0;
    int id_categoria = 0;
    int tipoSelected = -1;
    Manual manual;
    ActivityNuevoCursoBinding binding;
    PreferenceManager preferenceManager;
    private GlobalVariables gv;
    private String encodedImage;

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
            binding.txtPrecioCursoTecnicoNuevo.getEditText().setText(String.valueOf(manual.getPrecio()));
            tipoSelected = manual.getTipo() - 1;
            binding.spnTipoCurso.setSelection(tipoSelected);

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
        binding.btnCamaraCursoTecnicoNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
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
                            binding.ivImagenCursoTecnicoNuevo.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

    );
}