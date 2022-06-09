package com.servfix.manualesapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import static java.lang.Integer.parseInt;

public class CuadroDialogoSeleccionarFormaPago extends DialogFragment {
    FragmentManager fm;
    View mView;
    Context mContext;
    Dialog dialogo = null;

    ImageView btnPayPal;
    ImageView btnStripe;
    ImageView btnPaynet;
    ImageView btnOxxo;
    Button btnCancelar;

    public interface Confirmar {

        public void formaPagoConfirmada(int opcion);

    }
    Confirmar listener;

    public CuadroDialogoSeleccionarFormaPago(Context context, FragmentManager fm,View view) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Confirmar) context;
            //activity = getTargetFragment().getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cuadro_dialogo_seleccionar_formapago, null);
        Bundle b = getArguments();

        btnPayPal = (ImageView) v.findViewById(R.id.btnPagoPayPal);
        btnStripe = (ImageView) v.findViewById(R.id.btnPagoStripe);
        btnPaynet = (ImageView) v.findViewById(R.id.btnPagoPaynet);
        btnOxxo = (ImageView) v.findViewById(R.id.btnPagoOxxo);
        btnCancelar = (Button) v.findViewById(R.id.btnSalirDialogoFormaPago);

        //distancia = b.getDouble("distancia");
        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        btnPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aceptarSolicitud();
                dialogo.dismiss();
                listener.formaPagoConfirmada(1);

            }
        });

        btnStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aceptarSolicitud();
                dialogo.dismiss();
                listener.formaPagoConfirmada(2);

            }
        });

        btnPaynet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aceptarSolicitud();
                dialogo.dismiss();
                listener.formaPagoConfirmada(3);

            }
        });

        btnOxxo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////aceptarSolicitud();
                //dialogo.dismiss();
                //listener.formaPagoConfirmada(4);

            }
        });

        //mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaDialogoSolicitud);
        //mapFragment.getMapAsync(this);
        //initGoogleMap(savedInstanceState);

        return v;


    }
}
