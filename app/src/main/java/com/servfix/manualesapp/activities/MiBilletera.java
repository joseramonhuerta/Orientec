package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.databinding.ActivityMiBilleteraBinding;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MiBilletera extends BaseActivity {
    ActivityMiBilleteraBinding binding;
    String FechaInicio;
    String FechaFin;
    int lastYear;
    int lastMonth;
    int lastDay;
    View mView;
    String FinalJSonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMiBilleteraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mView = binding.getRoot();
        init();
        setListeners();
    }

    private void init(){
        final Calendar c = Calendar.getInstance();
        lastYear = c.get(Calendar.YEAR);
        lastMonth = c.get(Calendar.MONTH);
        lastDay = c.get(Calendar.DAY_OF_MONTH);

        getPeriodo(lastDay, lastMonth, lastYear);

        getDatosBilletera(mView);

    }

    private void getPeriodo(int lastday, int lastmonth, int lastyear){
        final String selectedEndDate = twoDigits(lastday) + "/" + twoDigits(lastmonth+1) + "/" + lastyear;
        FechaFin = selectedEndDate;

        String periodo = diaSemana(lastday,lastmonth+1, lastyear) + " " + String.valueOf(lastday) + " " + getMesName(lastmonth+1);

        final Calendar c2 = Calendar.getInstance();
        c2.set(lastyear, lastmonth, lastday);
        c2.add(Calendar.DATE, -6);
        int year = c2.get(Calendar.YEAR);
        int month = c2.get(Calendar.MONTH);
        int day = c2.get(Calendar.DAY_OF_MONTH);

        final String selectedInitDate  = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
        FechaInicio = selectedInitDate;

        periodo = diaSemana(day,month+1, year) + " " + String.valueOf(day) + " " + getMesName(month+1) + " - " + periodo;

        binding.txtPeriodoBilletera.setText(periodo);
    }

    private void getDatosBilletera(View view){
        final View vista = view;
        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "obtenerbilletera.php?id_usuario=" + String.valueOf(id_user) + "&fechainicio=" + parseFecha(FechaInicio) + "&fechafin=" + parseFecha(FechaFin);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;
                            // Calling method to parse JSON object.
                            new MiBilletera.ParseJSonDataClass(vista).execute();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Showing error message if something goes wrong.
                            Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            // Creating String Request Object.
            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
            // Passing String request into RequestQueue.
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners(){
        binding.layCuentaBancaria.setOnClickListener((View v) -> {
            Intent intent = new Intent(MiBilletera.this, CuentaBancaria.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.layCursosVendidos.setOnClickListener((View v) -> {
            Intent intent = new Intent(MiBilletera.this, CursosVendidos.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.btnRetirarBilletera.setOnClickListener((View v) -> {
            Intent intent = new Intent(MiBilletera.this, EstadoCuenta.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.btnAtrasBilletera.setOnClickListener( v-> onBackPressed());

        binding.btnSemanaAnterior.setOnClickListener((View v) -> {
            final Calendar c = Calendar.getInstance();
            c.set(lastYear, lastMonth, lastDay);
            c.add(Calendar.DATE, -6);
            lastYear = c.get(Calendar.YEAR);
            lastMonth = c.get(Calendar.MONTH);
            lastDay = c.get(Calendar.DAY_OF_MONTH);

            getPeriodo(lastDay, lastMonth, lastYear);

            getDatosBilletera(mView);
        });

        binding.btnSemanaSiguiente.setOnClickListener((View v) -> {
            final Calendar c = Calendar.getInstance();
            c.set(lastYear, lastMonth, lastDay);
            c.add(Calendar.DATE, +6);
            lastYear = c.get(Calendar.YEAR);
            lastMonth = c.get(Calendar.MONTH);
            lastDay = c.get(Calendar.DAY_OF_MONTH);

            getPeriodo(lastDay, lastMonth, lastYear);

            getDatosBilletera(mView);
        });
    }

    public String parseFecha(String fecha){
        String parsedDate = "";


        String newFecha = fecha;

        try {
            Date initDate = new SimpleDateFormat("dd/mm/yyyy").parse(newFecha);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            parsedDate = formatter.format(initDate).toString();
        } catch (ParseException ex) {

        }

        return parsedDate;

    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private String getMesName(int mes)
    {
        String letraD="";
        switch (mes){
            case 1: letraD = "Ene";
                break;
            case 2: letraD = "Feb";
                break;
            case 3: letraD = "Mar";
                break;
            case 4: letraD = "Abr";
                break;
            case 5: letraD = "May";
                break;
            case 6: letraD = "Jun";
                break;
            case 7: letraD = "Jul";
                break;
            case 8: letraD = "Ago";
                break;
            case 9: letraD = "Sep";
                break;
            case 10: letraD = "Oct";
                break;
            case 11: letraD = "Nov";
                break;
            case 12: letraD = "Dic";
                break;
        }

        return letraD;
    }

    private String diaSemana (int dia, int mes, int ano)
    {
        String letraD="";
        Calendar c = Calendar.getInstance();
        c.set(ano, mes-1, dia);
        int nD=c.get(Calendar.DAY_OF_WEEK);
        switch (nD){
            case 1: letraD = "Dom";
                break;
            case 2: letraD = "Lun";
                break;
            case 3: letraD = "Mar";
                break;
            case 4: letraD = "Mie";
                break;
            case 5: letraD = "Jue";
                break;
            case 6: letraD = "Vie";
                break;
            case 7: letraD = "Sab";
                break;
        }

        return letraD;
    }

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        public View view;
        public String msg;
        boolean success;
        JSONArray jsonArray,jsonArrayDatos;
        // Creating List of Subject class.


        public ParseJSonDataClass(View view) {
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
                    jsonArray = null;
                    jsonArrayDatos = null;
                    JSONObject jsonObject;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        this.success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });*/


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
            binding.progressBarBilletera.setVisibility(View.GONE);

           if(success){
               JSONObject jsonObjectDatos = null;
               double Cargos = 0.00f, Saldo = 0.00f;
               try {
                   jsonObjectDatos = jsonArray.getJSONObject(0);
                   Cargos = jsonObjectDatos.getDouble("cargos");
                   Saldo = jsonObjectDatos.getDouble("saldo");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               binding.txtImporteBilletera.setText("$ " + getPrecioFormatoMoneda(Cargos) + " mxn");
               binding.txtImporteRetirarBilletera.setText("Retirar $ " + getPrecioFormatoMoneda(Saldo) + " mxn");
           }else{
               Toast.makeText(MiBilletera.this,msg,Toast.LENGTH_SHORT).show();
           }
        }
    }
}