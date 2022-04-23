package com.servfix.manualesapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.adapters.ListViewAdapterCategorias;
import com.servfix.manualesapp.adapters.ListViewAdapterCategoriasBusqueda;
import com.servfix.manualesapp.classes.Categoria;
import com.servfix.manualesapp.utilities.GlobalVariables;

import org.json.JSONArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CuadroDialogoCategorias extends DialogFragment {
    Context mContext;
    FragmentManager fm;
    Dialog dialogo = null;
    View mView;

    TextView txtSearchCategoria;
    ImageView btnClearSearchCategoria;
    ListView listViewCategorias;
    Button btnAceptarCategoria;
    Button btnSalirCategoria;

    ProgressBar progressBar;

    String HTTP_URL = "";
    String FinalJSonObject ;

    int position_selected=-1;
    int id_categoria=0;
    String nombre_categoria="";
    String icono_categoria="";
    int tipo=0;

    public interface Actualizar {

        public void actualizaActividad(View view,int id_categoria,String nombre_categoria);
    }

    CuadroDialogoCategorias.Actualizar listener;
    Activity activity;

    public CuadroDialogoCategorias(Context context, FragmentManager fm, View view, int tipo) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
        this.tipo = tipo;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CuadroDialogoCategorias.Actualizar) getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.cuadro_dialogo_categorias, null);

        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        txtSearchCategoria = (TextView) v.findViewById(R.id.txtSearchCategoria);
        btnClearSearchCategoria = (ImageView) v.findViewById(R.id.btnClearSearchCategoria);

        btnAceptarCategoria = (Button) v.findViewById(R.id.btnAceptarCategorias);
        btnSalirCategoria = (Button) v.findViewById(R.id.btnSalirCategorias);

        listViewCategorias = (ListView) v.findViewById(R.id.listViewCategoriasBusqueda);

        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarCategoriasBusqueda);

        btnAceptarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
                listener.actualizaActividad(mView,id_categoria,nombre_categoria);
            }
        });

        btnSalirCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();;
            }
        });

        btnClearSearchCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchCategoria.setText("");

            }
        });

        listViewCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                position_selected = position;
                Categoria selItem = (Categoria) listViewCategorias.getAdapter().getItem(position);

                id_categoria =  selItem.getId_categoria();
                nombre_categoria = selItem.getNombre_categoria();
                icono_categoria = selItem.getIcono();
                //Toast.makeText(view.getContext(), desc+" ID: " + String.valueOf(idAsig)+ " IDPRo: "+String.valueOf(idPro), Toast.LENGTH_LONG).show();

                //Object lav = listView.getAdapter().getItem(position);
            }
        });

        loadClientes(v);

        return v;

    }

    private void loadClientes(final View view){
        progressBar.setVisibility(View.VISIBLE);

        GlobalVariables variablesGlobales = new GlobalVariables();
        HTTP_URL = variablesGlobales.URLServicio + "obtenercategorias.php";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new CuadroDialogoCategorias.ParseJSonDataClass(view.getContext()).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(view.getContext(),error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        List<Categoria> categoriasList;

        public ParseJSonDataClass(Context context) {

            this.context = context;
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
                        Categoria categoria;

                        // Defining CustomSubjectNamesList AS Array List.
                        categoriasList = new ArrayList<Categoria>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            categoria = new Categoria();

                            jsonObject = jsonArray.getJSONObject(i);

                            categoria.setId_categoria(Integer.parseInt(jsonObject.getString("id_categoria")));
                            categoria.setNombre_categoria(jsonObject.getString("nombre_categoria"));
                            GlobalVariables variablesGlobales = new GlobalVariables();
                            String URL = variablesGlobales.URLServicio + "images/" + jsonObject.getString("icono");
                            categoria.setIcono(URL);
                            categoria.setImagen_categoria(jsonObject.getString("imagen_categoria"));
                            categoriasList.add(categoria);
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
            //FragmentManager fm = getFragmentManager();
            final ListViewAdapterCategoriasBusqueda adapter = new ListViewAdapterCategoriasBusqueda(categoriasList, context);

            // Setting up all data into ListView.
            listViewCategorias.setAdapter(adapter);

            txtSearchCategoria.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //listView.getAdapter().getFilter().filter(s.toString());


                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //listView.getAdapter().
                    String text = txtSearchCategoria.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                    // listView.getAdapter().getFilter().filter(s.toString());
                }
            });

            // Hiding progress bar after all JSON loading done.
            progressBar.setVisibility(View.GONE);

        }
    }



}
