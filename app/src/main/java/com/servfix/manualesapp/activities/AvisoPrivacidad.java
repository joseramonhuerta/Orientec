package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.RecibirPDFStream;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.fragments.CuadroDialogoRankin;
import com.servfix.manualesapp.utilities.GlobalVariables;

public class AvisoPrivacidad extends AppCompatActivity {
    PDFView pdfView;
    ProgressBar progressBar;
    TextView txtTituloPDF;
    ImageView btnAtrasPDF;
    androidx.appcompat.widget.Toolbar myToolbar;
    Manual manual=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_privacidad);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarAvisoPrivacidad);
        setSupportActionBar(myToolbar);
        pdfView = findViewById(R.id.v_pdfAvisoPrivacidad);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarAvisoPrivacidad);
        btnAtrasPDF = (ImageView) findViewById(R.id.btnAtrasAvisoPrivacidad);

        GlobalVariables variablesGlobales = new GlobalVariables();
        String URL =  variablesGlobales.URLServicio;

        btnAtrasPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        String URLPDF = URL + "politica_privacidad_orientec.pdf";
        new RecibirPDFStream(pdfView, progressBar).execute(URLPDF);
    }

}