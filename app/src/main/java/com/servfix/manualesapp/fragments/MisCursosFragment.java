package com.servfix.manualesapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.activities.CarritoCompras;
import com.servfix.manualesapp.ChatSoporte;
import com.servfix.manualesapp.ClaseChat;
import com.servfix.manualesapp.DetalleManualPdf;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.adapters.ListViewAdapterManuales;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.ReproductorVimeo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MisCursosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    Context mContext;
    ImageView btnMisCursosCarrito;
    TextView txtSinManuales;
    ListView listView;
    static View mView;
    androidx.appcompat.widget.Toolbar myToolbar;
    ImageView btnSalir;
    String FinalJSonObject;
    SwipeRefreshLayout swipeContainer;
    String HTTP_URL;
    List<Manual> listManuales;

    SweetAlertDialog pDialogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mis_cursos, container, false);

        Activity a = getActivity();
        mView = view;

        if(a != null)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = getActivity().getApplicationContext();
        txtSinManuales = (TextView) view.findViewById(R.id.txtSinManuales);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainerMiscursos);
        listView = (ListView) view.findViewById(R.id.listviewMisCursos);
        btnMisCursosCarrito =  (ImageView) view.findViewById(R.id.btnMisCursosCarrito);
        swipeContainer.setOnRefreshListener(this);
        btnMisCursosCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getActivity(), CarritoCompras.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Manual manual = (Manual) listView.getItemAtPosition(position);

                int tipo = manual.getTipo();

                if(tipo == 1){
                    Intent intencion = new Intent(mContext, DetalleManualPdf.class);
                    intencion.putExtra("manual", (Serializable)manual);
                    intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intencion);

                }else if(tipo ==2){
                    Intent intencion = new Intent(mContext, ReproductorVimeo.class);
                    intencion.putExtra("manual", (Serializable)manual);
                    intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intencion);
                }else if(tipo == 3){
                    GlobalVariables gv = new GlobalVariables();
                    ClaseChat chat = new ClaseChat();

                    chat.id_manual = manual.getId_manual();
                    chat.id_usuario_manual = manual.getId_usuario_manual();
                    chat.id_usuario_sender = manual.getId_usuario();
                    chat.id_usuario_receiver = manual.getId_usuario_tecnico();
                    chat.nombre_manual = manual.getNombre_manual();
                    chat.nombre_usuario_sender = manual.getNombre_usuario();
                    chat.nombre_usuario_receiver = manual.getNombre_tecnico();
                    chat.imagen_receiver = manual.getImagen_tecnico();
                    chat.imagen_sender = manual.getImagen_usuario();
                    chat.id_usuario_firebase_receiver = manual.getId_usuario_firebase();
                    chat.id_usuario_firebase_sender = manual.getId_usuario_firebase_sender();
                    chat.calificacion = manual.getCalificacion();
                    //chat.setToken();


                    Intent intencion = new Intent(mContext, ChatSoporte.class);
                    intencion.putExtra("manual", (Serializable)chat);
                    intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intencion);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Tipo manual no válido.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //loadMisCursos(mView);
        return view;
    }

    public void loadMisCursos(View mView){
        final View vista = mView;
        pDialogo = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando...");
        pDialogo.setCancelable(false);
        pDialogo.show();
        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;

        HTTP_URL = variablesGlobales.URLServicio + "obtenermiscursos.php?id_usuario="+String.valueOf(id_user);
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;
                        // Calling method to parse JSON object.
                        new MisCursosFragment.ParseJSonDataClass(vista).execute();
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

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<Manual> manualesList;

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

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Manual manual;

                        // Defining CustomSubjectNamesList AS Array List.
                        manualesList = new ArrayList<Manual>();

                        GlobalVariables variablesGlobales = new GlobalVariables();

                        String URLPORTADA =  variablesGlobales.URLServicio;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            manual = new Manual();
                            jsonObject = jsonArray.getJSONObject(i);
                            manual.setId_usuario_manual(Integer.parseInt(jsonObject.getString("id_usuario_manual")));
                            manual.setId_manual(Integer.parseInt(jsonObject.getString("id_manual")));
                            manual.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            manual.setId_usuario_tecnico(Integer.parseInt(jsonObject.getString("id_usuario_tecnico")));
                            manual.setNombre_tecnico(jsonObject.getString("nombre_tecnico"));
                            manual.setNombre_usuario(jsonObject.getString("nombre_usuario"));
                            manual.setNombre_manual(jsonObject.getString("nombre_manual"));
                            manual.setDescripcion_manual(jsonObject.getString("descripcion_manual"));
                            manual.setPaginas(jsonObject.getString("paginas"));
                            manual.setNombre_pdf(jsonObject.getString("nombrepdf"));
                            manual.setPrecio(Double.parseDouble(jsonObject.getString("precio")));
                            manual.setTipo(Integer.parseInt(jsonObject.getString("tipo")));
                            manual.setTipo_descripcion(jsonObject.getString("tipo_descripcion"));
                            manual.setUrl(jsonObject.getString("url"));
                            manual.setImagen_tecnico(jsonObject.getString("imagen_tecnico"));
                            manual.setImagen_usuario(jsonObject.getString("imagen_sender"));
                            manual.setId_usuario_firebase(jsonObject.getString("id_usuario_firebase"));
                            manual.setId_usuario_firebase_sender(jsonObject.getString("id_usuario_firebase_sender"));
                            manual.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                            manual.setUrl_portada(GlobalVariables.URLServicio + "manuales/" + jsonObject.getString("id_manual") + "/"+ jsonObject.getString("url_portada"));
                            manual.setPuede_calificar(1);
                            manualesList.add(manual);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        pDialogo.dismiss();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                pDialogo.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            final ListViewAdapterManuales adapter = new ListViewAdapterManuales(manualesList, context);
            if(manualesList.size() > 0){
                txtSinManuales.setVisibility(View.GONE);
            }else{
                txtSinManuales.setVisibility(View.VISIBLE);
            }
            // Setting up all data into ListView.
            listView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
            pDialogo.dismiss();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMisCursos(mView);
    }

    @Override
    public void onRefresh() {
        loadMisCursos(mView);
    }

}