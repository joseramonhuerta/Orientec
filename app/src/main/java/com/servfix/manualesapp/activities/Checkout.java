package com.servfix.manualesapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.*;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Checkout extends BaseActivity {
    private static final String BACKEND_URL = "https://erp.servfix.com.mx/stripe-payment/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private String totalCarrito="0.00";
    public static SweetAlertDialog pDialogo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //pk_live_51ItcdaJc0kH60q11J0rh7a6dqlbiybDyKpDbsN8dre1tKk7NGgBIXcQX93KvWEZFj2d2ugdMFcBF8MWSPJuCjy3j00Gj0Etsqf
        //pk_test_51ItcdaJc0kH60q11ZGJJ48BSDQtWAjzNb1TsbZqoUDE07Jrv38XKWF2Nkkqne4tj0KDZjzPwdvGgaCjxxeDWN3te00aZy4QMuT
        setContentView(R.layout.activity_checkout);
        GlobalVariables gv = new GlobalVariables();
        String key_stripe = gv.STRIPE_PRODUCTION_KEY;
        totalCarrito = getIntent().getStringExtra("totalcarrito");
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(key_stripe)
        );
        startCheckOut();
    }

    private void startCheckOut(){

        MediaType mediaType = MediaType.get("application/json");
        /*
        String json = "{"
               + "\"amount\":\""+ totalCarrito +"\","
                + "\"currency\":\"MXN\","
                + "\"items\":["
               + "{\"id\":\"photo_subscription\"}"
                + "]"
                + "}";
        */

        double amount = Double.parseDouble(totalCarrito);
        Map<String, Object> payMap  = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency","mxn");
        payMap.put("amount",amount);
        payMap.put("payment_method","card");
        itemMap.put("id","photo_subscription");
        itemList.add(itemMap);
        payMap.put("items", itemList);
        String json = new Gson().toJson(payMap);



        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL /*+ "create-payment-intent"*/)
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallBack(this));

        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            pDialogo = new SweetAlertDialog(Checkout.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialogo.setTitleText("Procesando pago, espere...");
            pDialogo.setCancelable(false);
            pDialogo.show();
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if(params != null){
                ConfirmPaymentIntentParams confirmParams;
                confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params,paymentIntentClientSecret);

                stripe.confirmPayment(this, confirmParams);
            }else{
                pDialogo.dismiss();
                Toast.makeText(Checkout.this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallbackStripe(this));
    }

    private static final class PayCallBack implements Callback{
        @NonNull private final WeakReference<Checkout> activityRef;

        private PayCallBack(@NonNull Checkout activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            final Checkout activity = activityRef.get();
            if(activity == null){
                return;
            }
            activity.runOnUiThread(()->
                            Toast.makeText(activity, "error" + e.toString(), Toast.LENGTH_LONG).show()
             );
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
            final Checkout activity = activityRef.get();
            if(activity == null){
                return;
            }
            if(!response.isSuccessful()){
                activity.runOnUiThread(()->
                        Toast.makeText(activity, "error" + response.toString(), Toast.LENGTH_LONG).show()
                );
            }else{
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException{
        Gson gson = new Gson();
        Log.v("response", response.toString());
        Log.v("response.body", response.body().toString());
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
          Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret =  responseMap.get("clientSecret");

    }

    private static final class PaymentResultCallbackStripe implements ApiResultCallback<PaymentIntentResult>{
        @NonNull private final WeakReference<Checkout> activityRef;
        PaymentResultCallbackStripe(@NonNull Checkout activity){
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NotNull PaymentIntentResult result) {
            final Checkout activity = activityRef.get();
            pDialogo.dismiss();
            if(activity == null){
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if(status == PaymentIntent.Status.Succeeded){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Intent intent = new Intent();
                activity.setResult(RESULT_OK, intent);
                activity.finish();
            }else if(status == PaymentIntent.Status.RequiresPaymentMethod){
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).toString()
                );
            }

        }

        @Override
        public void onError(@NotNull Exception e) {
            final Checkout activity = activityRef.get();
            pDialogo.dismiss();
            if(activity == null){
                return;
            }
            activity.displayAlert("error", e.toString());
        }
    }

    private void displayAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.create().show();


    }


}