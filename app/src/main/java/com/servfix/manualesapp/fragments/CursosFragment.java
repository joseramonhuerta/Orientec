package com.servfix.manualesapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.servfix.manualesapp.classes.Categoria;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.adapters.GridViewAdapterCursos;
import com.servfix.manualesapp.GridViewScrollable;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.adapters.MyRecyclerViewAdapter;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.interfaces.RecyclerViewOnItemClickListener;
import com.servfix.manualesapp.adapters.SliderAdapterExample;
import com.servfix.manualesapp.classes.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CursosFragment extends Fragment /*implements SwipeRefreshLayout.OnRefreshListener*/{

    ListView listView;
    static View mView;
    androidx.appcompat.widget.Toolbar myToolbar;
    ImageView btnSalir;
    String FinalJSonObject;
    String FinalJSonObjectCategorias;
    String FinalJSonObjectCarrucel;
    SwipeRefreshLayout swipeContainer;
    String HTTP_URL;
    List<Manual> listManuales;

    EditText txtBusquedaCursos;
    TextView txtFiltros;
    ImageView btnBuscarCurso;
    ImageView btnLimpiarBusquedaCursos;
    ImageView btnFiltrosBusquedaCursos;
    Button btnComprarCurso;
    SliderView sliderView;
    ImageView ivBanner;

    GridViewScrollable gridView;
    RecyclerView listViewCategorias;
    Context mContext;

    int filtro = 0;
    int filtro_categoria = 0;
    SweetAlertDialog pDialogo;

    private SliderAdapterExample adapterCarrucel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cursos, container, false);
        Activity a = getActivity();
        mView = view;
        loadCategorias(mView);
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.mContext = getActivity().getApplicationContext();

        //swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainerCursos);
        gridView = (GridViewScrollable) view.findViewById(R.id.gvCursos);
        gridView.setNumColumns(2);
        gridView.setExpanded(true);
        txtBusquedaCursos = (EditText) view.findViewById(R.id.txtBusquedaCursos);
        txtFiltros = (TextView) view.findViewById(R.id.txtFiltros);
        btnLimpiarBusquedaCursos = (ImageView) view.findViewById(R.id.btnLimpiarBusqueda);
        btnBuscarCurso = (ImageView) view.findViewById(R.id.btnBuscarCurso);
        btnFiltrosBusquedaCursos = (ImageView) view.findViewById(R.id.btnFiltrosBusqueda);
        btnComprarCurso = (Button) view.findViewById(R.id.btnComprarCurso);
        listViewCategorias = (RecyclerView) view.findViewById(R.id.listViewCategorias);

        sliderView = view.findViewById(R.id.imageSlider);
        ivBanner = (ImageView) view.findViewById(R.id.ivBanner);

       // swipeContainer.setOnRefreshListener(this);
        btnFiltrosBusquedaCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFiltro(v);

            }
        });

        btnBuscarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCursos(mView);
            }
        });

        btnLimpiarBusquedaCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBusquedaCursos.setText("");
                loadCursos(mView);
            }
        });



        loadCarrucel(mView);
        loadBanner(mView);
        //loadCursos(mView);
        return view;
    }

    public void loadBanner(View view){
        GlobalVariables vg = new GlobalVariables();
        try {
            Glide.with(getContext())
                    .load(vg.URLServicio +  "images/banner.jpg")
                    .fitCenter()
                    .into(ivBanner);
        }catch (Exception e){

        }


    }

    public void loadCursos(View mView){
        final View vista = mView;
        actualizarFiltros();

        pDialogo = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        GlobalVariables variablesGlobales = new GlobalVariables();

        HTTP_URL = variablesGlobales.URLServicio + "obtenermanuales.php?filtro_tipo="+filtro+"&busqueda="+txtBusquedaCursos.getText().toString() + "&filtro_categoria=" + filtro_categoria + "&id_usuario=" + String.valueOf(variablesGlobales.id_usuario);
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new CursosFragment.ParseJSonDataClass(vista).execute();

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
        RequestQueue requestQueue = Volley.newRequestQueue(mView.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

    public void loadCategorias(View view){
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();

        HTTP_URL = variablesGlobales.URLServicio + "obtenercategorias.php";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectCategorias = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClassCategorias(vista).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    public void loadCarrucel(View view){
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();

        HTTP_URL = variablesGlobales.URLServicio + "obtenerimagenescarrucel.php";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectCarrucel = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClassCarrucel(vista).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    private void seleccionarFiltro(View view){
        String[] opciones = {"Todos", "Manuales", "Videos", "Asesoria"};
        //0=Recibido,1=Revision,2=Cotizacion,3=Reparacion,4=Reparado,5=Entregado,6=Devolucion
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
        builder.setTitle("Filtro");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtro = which;

                actualizarFiltros();
                loadCursos(mView);
            }
        });

        Dialog dialogo =  builder.create();
        dialogo.show();
    }

    public void actualizarFiltros(){
        String status="";
        switch (filtro) {
            case 0:
                status = "Todos";
                break;
            case 1:
                status = "Manuales";
                break;
            case 2:
                status = "Videos";
                break;
            case 3:
                status = "Asesorias";
                break;
        }

        String filtros = "Filtro: " + status;
        txtFiltros.setText(filtros);
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        List<Manual> manualesList;

        public ParseJSonDataClass(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                            manual.setPaginas(jsonObject.getString("paginas"));
                            manual.setNombre_pdf(jsonObject.getString("nombrepdf"));
                            String portada = URLPORTADA + "manuales/" + jsonObject.getString("id_manual") + "/portada.jpg";
                            manual.setPortada(portada);
                            manual.setPrecio(Double.parseDouble(jsonObject.getString("precio")));
                            manual.setTipo(Integer.parseInt(jsonObject.getString("tipo")));
                            manual.setTipo_descripcion(jsonObject.getString("tipo_descripcion"));
                            manual.setEsgratuito(Integer.parseInt(jsonObject.getString("esgratuito")));
                            manual.setObtenido(Integer.parseInt(jsonObject.getString("obtenido")));
                            manual.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                            manual.setNombre_tecnico(jsonObject.getString("nombre_usuario"));
                            manual.setImagen_tecnico(jsonObject.getString("imagen_usuario"));
                            manual.setId_usuario_tecnico(Integer.parseInt(jsonObject.getString("id_usuario_creador")));
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
            final GridViewAdapterCursos adapter = new GridViewAdapterCursos(manualesList, context);
            gridView.setAdapter(adapter);
            pDialogo.dismiss();
        }
    }

    private class ParseJSonDataClassCategorias extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        List<Categoria> categoriasList;

        public ParseJSonDataClassCategorias(View view) {
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
                if (FinalJSonObjectCategorias != null) {

                    JSONArray jsonArray = null;
                    try {

                        jsonArray = new JSONArray(FinalJSonObjectCategorias);

                        JSONObject jsonObject;

                        Categoria categoria;

                        categoriasList = new ArrayList<Categoria>();
                        categoria = new Categoria();
                        categoria.setId_categoria(0);
                        categoria.setNombre_categoria("Todas las categorías");
                        categoria.setIcono(GlobalVariables.URLServicio + "images/icono_todas.png");
                        categoriasList.add(categoria);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            categoria = new Categoria();

                            jsonObject = jsonArray.getJSONObject(i);

                            categoria.setId_categoria(Integer.parseInt(jsonObject.getString("id_categoria")));
                            categoria.setNombre_categoria(jsonObject.getString("nombre_categoria"));
                            categoria.setIcono(GlobalVariables.URLServicio + "images/" + jsonObject.getString("icono"));
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
            listViewCategorias.setAdapter(new MyRecyclerViewAdapter(categoriasList, new RecyclerViewOnItemClickListener() {

                @Override
                public void onClick(View v, int position) {
                    filtro_categoria = categoriasList.get(position).getId_categoria();
                    loadCursos(mView);
                }
            }, getActivity().getApplicationContext()));
            //Horizontal orientation.
            listViewCategorias.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private class ParseJSonDataClassCarrucel extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        List<SliderItem> carrucelList;

        public ParseJSonDataClassCarrucel(View view) {
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
                if (FinalJSonObjectCarrucel != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectCarrucel);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        SliderItem sliderItem;

                        // Defining CustomSubjectNamesList AS Array List.
                        carrucelList = new ArrayList<SliderItem>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            sliderItem = new SliderItem();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            sliderItem.setDescription(jsonObject.getString("descripcion_carrucel"));
                            sliderItem.setImageUrl(GlobalVariables.URLServicio + "images/carrucel/" + jsonObject.getString("imagen_carrucel"));
                            sliderItem.setUrl(jsonObject.getString("url_carrucel"));

                            // Adding subject list object into CustomSubjectNamesList.
                            carrucelList.add(sliderItem);
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

            //listViewTecnicos.setAdapter(adapter);

            //listViewCategorias.setAdapter(new MyRecyclerViewAdapter(categoriasList, new RecyclerViewOnItemClickListener() {
                adapterCarrucel = new SliderAdapterExample(getContext());

                sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setIndicatorSelectedColor(Color.WHITE);
                sliderView.setIndicatorUnselectedColor(Color.GRAY);
                sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                sliderView.startAutoCycle();

                sliderView.setSliderAdapter(adapterCarrucel);

                adapterCarrucel.updateItems(carrucelList);

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadCursos(mView);
    }

    /*
    @Override
    public void onRefresh() {
        loadCursos(mView);
    }
    */

}