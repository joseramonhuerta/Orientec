package com.servfix.manualesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.servfix.manualesapp.CuadroDialogoPagoOxxo;
import com.servfix.manualesapp.CuadroDialogoSeleccionarFormaPago;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.adapters.ListViewAdapterCarrito;
import com.servfix.manualesapp.classes.Carrito;
//import com.stripe.android.paymen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import com.mercadopago.android.px.core.MercadoPagoCheckout;
//import com.mercadopago.android.px.model.Item;
//import com.mercadopago.android.px.model.Payer;






public class CarritoCompras extends BaseActivity implements ListViewAdapterCarrito.AdapterCallback, CuadroDialogoSeleccionarFormaPago.Confirmar, CuadroDialogoPagoOxxo.Actualizar {
    View mView;
    Context mContext;
    StringRequest stringRequest;
    String FinalJSonObject;
    String HTTP_URL;
    Double totalCarrito = 0.00;
    ListViewAdapterCarrito adapter;
    //boolean listaCargada = false;
    SweetAlertDialog pDialogo;

    androidx.appcompat.widget.Toolbar toolbarCarritoCompras;
    ListView listviewCarritoCompras;

    ImageView btnAtrasCarritoCompras;
    Button btnPagarCarritoCompras;

    TextView txtImporteTotalCarrito, txtCantidadCarrito, txtIDCarrito;

    FragmentManager fm;
    FragmentTransaction ft;
    CuadroDialogoSeleccionarFormaPago dialogoFragment;
    CuadroDialogoSeleccionarFormaPago tPrev;



    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static final int MERCADOPAGO_REQUEST_CODE = 7272;
    private static final int STRIPE_REQUEST_CODE = 7373;

    private static final String TAG = "CarritoCompras";


    private static PayPalConfiguration config =  new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION).clientId(GlobalVariables.PAYPAL_CLIENT_ID).acceptCreditCards(true);

    String importeTotalCarrito ="";
    int id_carrito_compras = 0;
    int cantidad_carrito = 0;
    int formapago = 0;
    int esgratuito = 0;

    @Override
    public void formaPagoConfirmada(int tipo){
        this.formapago = tipo;
        Log.i(TAG, "formapago: " + tipo);

        if(tipo == 1){
            procesarPagoPayPal(mView);
        }else if(tipo == 2){
            if(!validarImporteStripe()){
                Toast.makeText(getApplicationContext(), "El importe debe ser mayor a $ 9.99", Toast.LENGTH_SHORT).show();
                return;
            }
            procesarStripe(mView);
        }else if(tipo == 3){
            Log.i(TAG, "procesarPagoPaynet");
            procesarPagoPaynet(mView);
        }else if(tipo == 4){
            Log.i(TAG, "procesarPagoOxxo");
            procesarPagoOxxo(mView);
        }
    }

    private boolean validarImporteStripe() {
        boolean valido = false;

        if(totalCarrito >= 10.00){
            valido = true;
        }else{
            valido = false;
        }

        return valido;
    }

    @Override
    public void onMethodCallback(View v) {
        loadCarrito(v);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_compras);

        //MercadoPago.SDK.configure("ENV_ACCESS_TOKEN");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewCarritoCompras);
        mContext = getApplicationContext();
        toolbarCarritoCompras = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarCarritoCompras);
        setSupportActionBar(toolbarCarritoCompras);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        mContext.startService(intent);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        listviewCarritoCompras = (ListView) findViewById(R.id.listviewCarritoCompras);

        btnAtrasCarritoCompras = (ImageView) findViewById(R.id.btnAtrasCarritoCompras);
        btnPagarCarritoCompras = (Button) findViewById(R.id.btnPagarCarritoCompras);
        txtImporteTotalCarrito = (TextView) findViewById(R.id.txtImporteTotalCarrito);
        txtIDCarrito = (TextView) findViewById(R.id.txtIdCarrito);
        txtCantidadCarrito = (TextView) findViewById(R.id.txtCantidadCarrito);

        txtImporteTotalCarrito.setText("$ " + getPrecioFormatoMoneda(totalCarrito));
        txtIDCarrito.setText(String.valueOf(id_carrito_compras));
        txtCantidadCarrito.setText(String.valueOf(cantidad_carrito));
        /*
        PaymentConfiguration.init(

                getApplicationContext(),

                "pk_test_wk6O7Cc5k3McBIG2Hut2irGs"

        );*/

        btnAtrasCarritoCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPagarCarritoCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() < 1) {
                    SweetAlertDialog sDialogo = new SweetAlertDialog(CarritoCompras.this);
                    sDialogo.setTitleText("No hay elementos en el carrito");
                    sDialogo.show();

                } else {

                    SweetAlertDialog sDialog = new SweetAlertDialog(CarritoCompras.this, SweetAlertDialog.WARNING_TYPE);
                    sDialog.setTitleText("Desea pagar el carrito?");
                    //sDialog.setContentText("No se podra recuperar si es eliminada");
                    sDialog.setConfirmText("SI");
                    sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            //procesarPagoPayPal(v);
                            //procesarMercadoPago(v);
                            //pagarCarrito(v);

                            seleccionarFormaPago();

                        }
                    })
                            .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            });
                    sDialog.show();
                }
            }
        });

        loadCarrito(mView);
    }

    public void seleccionarFormaPago(){
        FragmentManager fm = getSupportFragmentManager();

        CuadroDialogoSeleccionarFormaPago editNameDialogFragment = new CuadroDialogoSeleccionarFormaPago(mContext, fm, mView);

        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    public void  procesarStripe(View view){
        long importeTotal = Math.round(totalCarrito * 100);
        Intent intent = new Intent(CarritoCompras.this, Checkout.class);
        intent.putExtra("totalcarrito", String.valueOf(importeTotal));
        startActivityForResult(intent, STRIPE_REQUEST_CODE);
    }

    private void startMercadoPagoCheckout(final String checkoutPreferenceId) {
        new MercadoPagoCheckout.Builder("TEST-1e4d8ba6-b523-433d-8afd-aac6349e98cf", checkoutPreferenceId).build()
                .startPayment(CarritoCompras.this, MERCADOPAGO_REQUEST_CODE);
    }



    public void procesarPagoPayPal(View view){
        GlobalVariables gv = new GlobalVariables();
        String pagado_por = "pagado por " + gv.nombre_usuario + " " + gv.paterno + " " + gv.materno;
        importeTotalCarrito = txtImporteTotalCarrito.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(importeTotalCarrito)),"MXN", pagado_por,PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(CarritoCompras.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    public void procesarPagoOxxo(View view){
        FragmentManager fm = getSupportFragmentManager();
        CuadroDialogoPagoOxxo editNameDialogFragment = new CuadroDialogoPagoOxxo(mContext, mView, totalCarrito);
        editNameDialogFragment.show(fm, "fragment_edit_oxxo");
    }

    public void procesarPagoPaynet(View view){
        //FragmentManager fm = getSupportFragmentManager();
        //CuadroDialogoPagoOxxo editNameDialogFragment = new CuadroDialogoPagoOxxo(mContext, mView, totalCarrito);
        //editNameDialogFragment.show(fm, "fragment_edit_oxxo");

        final View vista = view;

        pDialogo = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Procesando pago, espere...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "generarpagoopenpay.php?id_usuario=" + String.valueOf(id_user) + "&importe=" + String.valueOf(totalCarrito);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;

                            // Calling method to parse JSON object.
                            new CarritoCompras.ParseJSonDataClassGenerarPagoPaynet(vista).execute();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        pagarCarrito(mView,"","");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
            else if(resultCode == RESULT_CANCELED)
                Toast.makeText(this,"Cancelada",  Toast.LENGTH_SHORT).show();
        }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalida", Toast.LENGTH_SHORT).show();

        if (requestCode == MERCADOPAGO_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);

                //Done!
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getExtras() != null
                        && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError =
                            (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);

                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }

        if(requestCode == STRIPE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                    pagarCarrito(mView,"", "");
            }else if(resultCode == RESULT_CANCELED)
                Toast.makeText(this,"Cancelado",  Toast.LENGTH_SHORT).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadCarrito(View mView){
        final View vista = mView;
        totalCarrito = 0.00;
        cantidad_carrito = 0;
        id_carrito_compras = 0;
        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;

        HTTP_URL = variablesGlobales.URLServicio + "obtenercarritocompras.php?id_usuario="+String.valueOf(id_user);
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClass(vista).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);

        //this.limite += 5;
    }

    @Override
    public void actualizaActividad(View mView) {
        pagarCarrito(mView,"","");
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<Carrito> carritoList;

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
                    JSONArray jsonArray = null;
                    try {



                        // Creating Subject class object.
                        Carrito carrito;

                        // Defining CustomSubjectNamesList AS Array List.
                        carritoList = new ArrayList<Carrito>();

                        GlobalVariables variablesGlobales = new GlobalVariables();

                        String URLPORTADA =  variablesGlobales.URLServicio;

                        // Adding JSON response object into JSON array.
                        //jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject,jsonObjectDatos, jsonObjectDetalle;
                        JSONArray jsonArrayDetalles;

                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
                        String msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        if(success) {
                            jsonObjectDatos = jsonArray.getJSONObject(0);
                            id_carrito_compras = Integer.parseInt(jsonObjectDatos.optString("id_carrito_compras"));
                            jsonArrayDetalles = jsonObjectDatos.getJSONArray("detalles");
                            cantidad_carrito = jsonArrayDetalles.length();
                            for (int i = 0; i < jsonArrayDetalles.length(); i++) {

                                carrito = new Carrito();

                                jsonObjectDetalle = jsonArrayDetalles.getJSONObject(i);

                                //Storing ID into subject list.
                                carrito.setId_carrito_compras_detalle(Integer.parseInt(jsonObjectDetalle.getString("id_carrito_compras_detalle")));
                                carrito.setId_carrito_compras(Integer.parseInt(jsonObjectDetalle.getString("id_carrito_compras")));
                                carrito.setId_manual(Integer.parseInt(jsonObjectDetalle.getString("id_manual")));
                                carrito.setNombre_manual(jsonObjectDetalle.getString("nombre_manual"));
                                Double precio = Double.parseDouble(jsonObjectDetalle.getString("precio"));
                                totalCarrito += precio;
                                carrito.setPrecio(precio);
                                //String portada = URLPORTADA + "/manuales/" + jsonObjectDetalle.getString("id_manual") + "/portada.jpg";
                                carrito.setPortada(jsonObjectDetalle.getString("imagen_miniatura"));

                                // Adding subject list object into CustomSubjectNamesList.
                                carritoList.add(carrito);

                            }
                        }else{
                            SweetAlertDialog sDialogo = new SweetAlertDialog(context);
                            sDialogo.setTitleText(msg);
                            sDialogo.show();
                        }

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
            adapter = new ListViewAdapterCarrito(carritoList, context, view);
            //listaCargada = true;
            listviewCarritoCompras.setAdapter(adapter);
            txtImporteTotalCarrito.setText(String.valueOf(totalCarrito));
            txtIDCarrito.setText(String.valueOf(id_carrito_compras));
            txtCantidadCarrito.setText(String.valueOf(cantidad_carrito));



        }
    }

    public void recargarCarritoCompras(View v){
        loadCarrito(v);
    }

    public void pagarCarrito(View view, String referencia, String id_openpay){
        Log.i(TAG, "pagarCarrito");
        final View vista = view;

        pDialogo = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Procesando, espere...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String HTTP_URL;

            int id_user = variablesGlobales.id_usuario;

            HTTP_URL = variablesGlobales.URLServicio + "pagarcarrito.php?id_carrito_compras=" + String.valueOf(this.id_carrito_compras) + "&id_openpay=" + id_openpay + "&referencia_openpay=" + referencia + "&formapago=" + String.valueOf(formapago) + "&id_manual=" + String.valueOf(0) + "&id_usuario=" + String.valueOf(0);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response;

                            // Calling method to parse JSON object.
                            new CarritoCompras.ParseJSonDataClassPagarCarrito(vista).execute();

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

    public String getPrecioFormatoMoneda(double precio){
        String precioFormateado = "";
        DecimalFormat form = new DecimalFormat("0.00");
        precioFormateado = String.valueOf(form.format(precio));
        return precioFormateado;
    }

    private class ParseJSonDataClassPagarCarrito extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        // Creating List of Subject class.


        public ParseJSonDataClassPagarCarrito(View view) {
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
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
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
            loadCarrito(view);
            pDialogo.dismiss();
            //activarDesactivarBotones(btnLimpiar, 1);
            SweetAlertDialog sDialogo = new SweetAlertDialog(context);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    private class ParseJSonDataClassGenerarPagoPaynet extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public String reference;
        public String id;

        public String creation_date;
        public String operation_date;
        public String transaction_type;
        public String status;
        public String amount;
        public String currency;
        public String barcode_url;

        public boolean success;

        // Creating List of Subject class.


        public ParseJSonDataClassGenerarPagoPaynet(View view) {
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
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;

                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        success = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        if(success) {

                            jsonObjectDatos = jsonArray.getJSONObject(0);
                            creation_date = jsonObjectDatos.optString("creation_date");
                            operation_date = jsonObjectDatos.optString("operation_date");
                            transaction_type = jsonObjectDatos.optString("transaction_type");
                            status = jsonObjectDatos.optString("status");
                            amount = jsonObjectDatos.optString("amount");
                            currency = jsonObjectDatos.optString("currency");
                            barcode_url = jsonObjectDatos.optString("barcode_url");

                            reference = jsonObjectDatos.optString("reference");
                            id = jsonObjectDatos.optString("id");


                        }

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
            pDialogo.dismiss();

            if(success){
                Log.i(TAG, "ParseJSonDataClassGenerarPagoPaynet - success");
                pagarCarrito(mView, reference, id);

                GlobalVariables vg = new GlobalVariables();
                //Generar recibo propio
                String Url = vg.URLServicio + "recibopaynet.php?amount=" + amount + "&currency=" + currency + "&reference=" + reference + "&barcode_url=" + barcode_url;
                //Generar recibo con servicio openpay
                //String Url = "https://sandbox-dashboard.openpay.mx/paynet-pdf/" + vg.MERCHANT_ID_SANDBOX + "/" + reference;


                Uri uri = Uri.parse(Url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }else{
                SweetAlertDialog sDialogo = new SweetAlertDialog(context);
                sDialogo.setTitleText(this.msg);
                sDialogo.show();
            }
            loadCarrito(view);
        }
    }


}