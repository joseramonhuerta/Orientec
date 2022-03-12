package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class DetalleManualWeb extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_manual_web);



        webView = (WebView) findViewById(R.id.wvManual);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url = "https://erp.servfix.com.mx/ws/manualesapp/prueba.html";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new MyAppWebViewClient());



    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}