package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
/*
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;*/
import com.servfix.manualesapp.BuildConfig;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CheckoutPaypal extends AppCompatActivity {
    //PayPalButton payPalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_paypal);
/*
        Intent intent = getIntent();
        String pagadopor = intent.getExtras().getString("pagadopor");
        String importe = intent.getExtras().getString("importe");

        payPalButton = findViewById(R.id.payPalButton);

        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                GlobalVariables.PAYPAL_CLIENT_ID,
                GlobalVariables.PAYPAL_ENVIROMENT,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.MXN,
                UserAction.PAY_NOW,
                new SettingsConfig(true, false)
        );
        PayPalCheckout.setConfig(config);

        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.MXN)
                                                        .value(importe)
                                                        .build()
                                        )
                                        .build()
                        );

                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },

                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));

                            }
                        });
                    }
                }

        );
*/
    }
}