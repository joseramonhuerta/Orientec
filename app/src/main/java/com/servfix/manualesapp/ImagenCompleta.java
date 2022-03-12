package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.servfix.manualesapp.adapters.GridViewAdapterImagenes;
import com.squareup.picasso.Picasso;

public class ImagenCompleta extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar myToolbar;
    ImageView btnAtras;
    ImageView ivImagenCompleta;
    TextView txtTitulo;
    GridViewAdapterImagenes gridViewAdapterImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_completa);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarDetalleManual);
        setSupportActionBar(myToolbar);

        btnAtras = (ImageView) findViewById(R.id.btnAtrasImagenCompleta);
        ivImagenCompleta = (ImageView) findViewById(R.id.ivImagenCompleta);
        txtTitulo = (TextView) findViewById(R.id.txtTituloImagenCompleta);

        Intent intent = getIntent();
        String img = intent.getExtras().getString("imagen");
        String titulo = intent.getExtras().getString("titulo");
        //gridViewAdapterImagenes = new GridViewAdapterImagenes(this);
        Picasso.get().load(img)
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(ivImagenCompleta);

        txtTitulo.setText(titulo);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
}