package com.servfix.manualesapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.PerfilUsuario;
import com.servfix.manualesapp.activities.AvisoPrivacidad;
import com.servfix.manualesapp.activities.MiBilletera;
import com.servfix.manualesapp.activities.MisChats;
import com.servfix.manualesapp.activities.MisCursosTecnico;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.classes.User;
import com.servfix.manualesapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {
    int id_usuario;
    GlobalVariables vg;

    TextView txtNombreUsuario, txtPaterno, txtCelular, txtEmail;
    LinearLayout btnSalir;
    FrameLayout layoutImage;
    ImageView ivProfile;
    CardView layEditarPerfil;
    CardView layCursos;
    CardView layChats;
    CardView layBilletera;
    CardView layAvisoPrivacidad;
    ProgressBar progressBarMiCuenta;
    View mView;

    String encodedImage;

    String FinalJSonObject;
    StringRequest stringRequest;

    private PreferenceManager preferenceManager;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        mView = view;
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txtNombreUsuario = (TextView) view.findViewById(R.id.txtNombreUsuarioPerfil);
        txtPaterno = (TextView) view.findViewById(R.id.txtApellidosUsuarioPerfil);
        txtEmail = (TextView) view.findViewById(R.id.txtEmailPerfil);
        txtCelular = (TextView) view.findViewById(R.id.txtCelularPerfil);
        ivProfile = (ImageView) view.findViewById(R.id.ivProfilePerfil);
        btnSalir = (LinearLayout) view.findViewById(R.id.laySalir);
        layEditarPerfil = (CardView) view.findViewById(R.id.layEditarPerfil);
        layCursos = (CardView) view.findViewById(R.id.layCursos);
        layChats = (CardView) view.findViewById(R.id.layChats);
        layBilletera = (CardView) view.findViewById(R.id.layBilletera);
        layAvisoPrivacidad = (CardView) view.findViewById(R.id.layAvisoPrivacidad);
        progressBarMiCuenta = (ProgressBar) view.findViewById(R.id.progressBarMiCuenta);

        preferenceManager = new PreferenceManager(getContext());

        vg = new GlobalVariables();
        id_usuario = vg.id_usuario;

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clear();
                getActivity().finish();
            }
        });

        layEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PerfilUsuario.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

        layCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MisCursosTecnico.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

        layChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MisChats.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

        layBilletera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MiBilletera.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

        layAvisoPrivacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AvisoPrivacidad.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });

        //loadPerfil(view);
        setPerfilValues();
        return view;
    }

    private void setPerfilValues(){
        txtNombreUsuario.setText(preferenceManager.getString(Constants.KEY_NOMBRE_USUARIO));
        txtPaterno.setText(preferenceManager.getString(Constants.KEY_PATERNO_USUARIO) + " " + preferenceManager.getString(Constants.KEY_MATERNO_USUARIO));
        txtEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL_USUARIO));
        txtCelular.setText(preferenceManager.getString(Constants.KEY_CELULAR));

        if(preferenceManager.getString(Constants.KEY_IMAGEN).length() > 0) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGEN), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ivProfile.setImageBitmap(bitmap);
        }
        progressBarMiCuenta.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setPerfilValues();
    }

    /*
    private void loadPerfil(View vista){

        GlobalVariables gv = new GlobalVariables();

        String HTTP_URL =gv.URLServicio + "obtenerperfilusuario.php?";
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new PerfilFragment.ParseJSonDataClass(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String id = String.valueOf(id_usuario);

                Map<String, String> parametros = new HashMap<>();

                parametros.put("id_usuario", id);

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public boolean success;
        public User user;

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
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        this.success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                        jsonArrayDatos = jsonObject.getJSONArray("datos");
                        jsonObjectDatos = jsonArrayDatos.getJSONObject(0);
                        user = new User();
                        //nombre_usuario, paterno_usuario, materno_usuario, celular,email_usuario,imagen
                        id_usuario = Integer.parseInt(jsonObjectDatos.optString("id_usuario"));
                        user.setId_usuario(Integer.parseInt(jsonObjectDatos.optString("id_usuario")));
                        user.setNombre(jsonObjectDatos.optString("nombre_usuario"));
                        user.setPaterno(jsonObjectDatos.optString("paterno_usuario"));
                        user.setMaterno(jsonObjectDatos.optString("materno_usuario"));
                        user.setCelular(jsonObjectDatos.optString("celular"));
                        user.setUsuario(jsonObjectDatos.optString("email_usuario"));
                        user.setImagen(jsonObjectDatos.optString("imagen"));

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
            super.onPostExecute(result);

            if(this.success){

                txtNombreUsuario.setText(user.getNombre());
                txtPaterno.setText(user.getPaterno() + " " + user.getMaterno());
                txtEmail.setText(user.getUsuario());
                txtCelular.setText(user.getCelular());

                if(user.getImagen().length() > 0) {
                    byte[] bytes = Base64.decode(user.getImagen(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ivProfile.setImageBitmap(bitmap);
                }
            }else{
                Toast.makeText(getActivity().getApplicationContext(), this.msg, Toast.LENGTH_SHORT).show();
            }

            progressBarMiCuenta.setVisibility(View.GONE);
        }
    }
    */
}