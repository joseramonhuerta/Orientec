package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.adapters.ListViewAdapterManuales;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listView;
    static View mView;
    androidx.appcompat.widget.Toolbar myToolbar;
    ImageView btnSalir;
    ProgressDialog pDialogo = null;
    String FinalJSonObject ;
    SwipeRefreshLayout swipeContainer;
    String HTTP_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarManuelaes);
        setSupportActionBar(myToolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainer);

        listView = (ListView) findViewById(R.id.listViewManuales);
        mView = (View) findViewById(R.id.viewPrincipal);
        btnSalir = (ImageView) findViewById(R.id.btnSalir);


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        swipeContainer.setOnRefreshListener(this);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Manual manual = (Manual) listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                //Toast.makeText(getApplicationContext(),orden.nombre_cliente,Toast.LENGTH_LONG).show();
                Intent intencion = new Intent(getApplicationContext(), DetalleManualPdf.class);
                intencion.putExtra("ID", manual.getId_manual());
                intencion.putExtra("nombre_manual", manual.getNombre_manual());
                intencion.putExtra("nombre_pdf", manual.getNombre_pdf());
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

        loadManuales(mView);
    }



    public void loadManuales(View view){
        final View vista = view;


        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;



        HTTP_URL = variablesGlobales.URLServicio + "obtenermanuales.php?id_usuario="+String.valueOf(id_user);
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
                        Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

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

                        GlobalVariables variablesGlobales = new GlobalVariables();

                        String URLPORTADA =  variablesGlobales.URLServicio;

                        // Defining CustomSubjectNamesList AS Array List.
                        manualesList = new ArrayList<Manual>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            manual = new Manual();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.

                            manual.setId_manual(Integer.parseInt(jsonObject.getString("id_manual")));
                            manual.setNombre_manual(jsonObject.getString("nombre_manual"));
                            manual.setDescripcion_manual(jsonObject.getString("descripcion_manual"));
                            manual.setPaginas(jsonObject.getString("paginas") + " paginas");
                            manual.setNombre_pdf(jsonObject.getString("nombrepdf"));
                            String portada = URLPORTADA + "/manuales/" + jsonObject.getString("id_manual") + "/portada.jpg";

                            manual.setPortada(portada);
                            // Adding subject list object into CustomSubjectNamesList.
                            manualesList.add(manual);
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
            // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.

            //Fragment ff = Entregar.this;
            //fm.beginTransaction().set
            final ListViewAdapterManuales adapter = new ListViewAdapterManuales(manualesList, context);

            // Setting up all data into ListView.
            listView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);



        }
    }


    @Override
    public void onResume(){
        super.onResume();
        loadManuales(mView);
    }


    @Override
    public void onRefresh() {
        loadManuales(mView);
    }
}