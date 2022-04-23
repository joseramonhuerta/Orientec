package com.servfix.manualesapp;

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
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.fragments.CuadroDialogoRankin;
import com.servfix.manualesapp.utilities.GlobalVariables;

public class DetalleManualPdf extends AppCompatActivity {
    PDFView pdfView;
    ProgressBar progressBar;
    TextView txtTituloPDF;
    ImageView btnAtrasPDF;
    androidx.appcompat.widget.Toolbar myToolbar;
    Manual manual=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_manual_p_d_f);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarDetalleManualPdf);
        setSupportActionBar(myToolbar);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarPDF);
        pdfView = findViewById(R.id.v_pdf);
        txtTituloPDF = (TextView) findViewById(R.id.txtTituloManualPDF);
        btnAtrasPDF = (ImageView) findViewById(R.id.btnAtrasPDF);

        //Bundle extras = getIntent().getExtras();

        Intent intent = getIntent();
        manual = (Manual) intent.getExtras().getSerializable("manual");

        String nombrePdf = manual.getNombre_pdf();
        int id_manual = manual.getId_manual();
        String nombre_manual = manual.getNombre_manual();

        txtTituloPDF.setText(nombre_manual);

        GlobalVariables variablesGlobales = new GlobalVariables();
        String URL =  variablesGlobales.URLServicio;

        btnAtrasPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        String URLPDF = URL + "manuales/" + String.valueOf(id_manual) + "/" + nombrePdf;
        new RecibirPDFStream(pdfView, progressBar).execute(URLPDF);

        if(!(manual.getCalificacion() > 0)){
            calificar();
        }

    }

    private void calificar(){
        FragmentManager fm = getSupportFragmentManager();

        CuadroDialogoRankin editNameDialogFragment = new CuadroDialogoRankin(getApplicationContext(), manual.getId_usuario_manual(), manual.getId_manual());

        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
}