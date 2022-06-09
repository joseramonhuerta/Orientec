package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.adapters.ListViewAdapterManualesTecnico;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.databinding.ActivityChatSoporteBinding;
import com.servfix.manualesapp.databinding.ActivityMisCursosTecnicoBinding;
import com.servfix.manualesapp.fragments.CursosTecnicoFragment;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MisCursosTecnico extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    Context mContext;
    View mView;
    SwipeRefreshLayout swipeContainer;
    String HTTP_URL, FinalJSonObject;
    List<Manual> listManuales;
    SweetAlertDialog pDialogo;

    ActivityMisCursosTecnicoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityMisCursosTecnicoBinding.inflate(getLayoutInflater());
        mView = binding.getRoot();
        setContentView(mView);

        binding.srlContainerCursosTecnico.setOnRefreshListener(this);

        //loadMisCursos(mView);

        setListeners();
    }

    private void setListeners(){
        binding.btnAgregarCursoTecnico.setOnClickListener((View v) -> {
            Manual manual = new Manual();
            manual.setId_manual(0);
            Intent intencion = new Intent(MisCursosTecnico.this, NuevoCurso.class);
            intencion.putExtra("manual", (Serializable)manual);
            intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intencion);
        });

        binding.btnAtrasMisCursosTecnico.setOnClickListener(v -> onBackPressed());

        binding.listviewCursosTecnico.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Manual manual = (Manual) binding.listviewCursosTecnico.getItemAtPosition(position);

                Intent intencion = new Intent(MisCursosTecnico.this, NuevoCurso.class);
                intencion.putExtra("manual", (Serializable)manual);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });
    }

    private void loadMisCursos(View mView) {
        final View vista = mView;

        pDialogo = new SweetAlertDialog(MisCursosTecnico.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;

        HTTP_URL = variablesGlobales.URLServicio + "obtenermiscursostecnico.php?id_usuario="+String.valueOf(id_user);
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new MisCursosTecnico.ParseJSonDataClass(vista).execute();

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

                            manual.setId_manual(Integer.parseInt(jsonObject.getString("id_manual")));
                            manual.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            manual.setNombre_manual(jsonObject.getString("nombre_manual"));
                            manual.setDescripcion_manual(jsonObject.getString("descripcion_manual"));
                            manual.setPaginas(jsonObject.getString("paginas"));
                            manual.setNombre_pdf(jsonObject.getString("nombrepdf"));
                            //manual.setPortada(jsonObject.getString("imagen_miniatura"));
                            //manual.setImagen_detalle(jsonObject.getString("imagen_detalle"));
                            manual.setPrecio(Double.parseDouble(jsonObject.getString("precio")));
                            manual.setTipo(Integer.parseInt(jsonObject.getString("tipo")));
                            manual.setTipo_descripcion(jsonObject.getString("tipo_descripcion"));
                            manual.setUrl(jsonObject.getString("url"));
                            manual.setId_categoria(Integer.parseInt(jsonObject.getString("id_categoria")));
                            manual.setEsgratuito(Integer.parseInt(jsonObject.getString("esgratuito")));
                            manual.setNombre_categoria(jsonObject.getString("nombre_categoria"));
                            String url_portada = jsonObject.getString("url_portada");
                            String url_detalle = jsonObject.getString("url_detalle");

                            if(url_portada != null && !url_portada.isEmpty() && !url_portada.equals("null"))
                                manual.setUrl_portada(GlobalVariables.URLServicio + "manuales/" + jsonObject.getString("id_manual") + "/"+ url_portada);
                            if(url_detalle != null && !url_detalle.isEmpty() && !url_detalle.equals("null"))
                                manual.setUrl_detalle(GlobalVariables.URLServicio + "manuales/" + jsonObject.getString("id_manual") + "/"+url_detalle);

                            manual.setStatus_manual(Integer.parseInt(jsonObject.getString("status_manual")));

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
            pDialogo.dismiss();
            final ListViewAdapterManualesTecnico adapter = new ListViewAdapterManualesTecnico(manualesList, context);

            if(manualesList.size() > 0){
                binding.txtSinCursos.setVisibility(View.GONE);
            }else{
                binding.txtSinCursos.setVisibility(View.VISIBLE);
            }

            binding.listviewCursosTecnico.setAdapter(adapter);
            binding.srlContainerCursosTecnico.setRefreshing(false);




        }
    }

    @Override
    public void onRefresh() {
        loadMisCursos(mView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMisCursos(mView);
    }
}