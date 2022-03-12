package com.servfix.manualesapp;

import android.app.Application;
import com.stripe.android.PaymentConfiguration;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51ItcdaJc0kH60q11ZGJJ48BSDQtWAjzNb1TsbZqoUDE07Jrv38XKWF2Nkkqne4tj0KDZjzPwdvGgaCjxxeDWN3te00aZy4QMuT"
        );
    }
}
