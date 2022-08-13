package com.servfix.manualesapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.firebase.MessagingService;
import com.servfix.manualesapp.fragments.CursosFragment;
import com.servfix.manualesapp.fragments.MisCursosFragment;
import com.servfix.manualesapp.fragments.PerfilFragment;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class MenuPrincipal extends BaseActivity {
    BottomNavigationView mMenu;
    int itemSelected;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSeletedFragment(new CursosFragment(), "CursosFragment", R.id.menu_cursos);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mMenu = (BottomNavigationView) findViewById(R.id.menu_principal);

        mMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_cursos & itemSelected != item.getItemId()){
                    showSeletedFragment(new CursosFragment(), "CursosFragment", item.getItemId());
                }

                if(item.getItemId() == R.id.menu_miscursos & itemSelected != item.getItemId()){
                    showSeletedFragment(new MisCursosFragment(), "MisCursosFragment", item.getItemId());
                }
                /*
                if(item.getItemId() == R.id.menu_carrito & itemSelected != item.getItemId()){
                    showSeletedFragment(new CarritoFragment(), "CarritoFragment", item.getItemId());
                }
                */
                if(item.getItemId() == R.id.menu_perfil & itemSelected != item.getItemId()){
                    showSeletedFragment(new PerfilFragment(), "PerfilFragment", item.getItemId());
                }
                return true;
            }
        });

        getToken();

    }

    private void showSeletedFragment(Fragment fragment, String tag, int item){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        itemSelected = item;
    }

    @Override public void onBackPressed() {

    }
    /*
    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MenuPrincipal.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(@NonNull InstanceIdResult instanceIdResult) {
                String updatedToken = instanceIdResult.getToken();
                updateToken(updatedToken);
            }
        });
    }

    private void updateToken(String token){
        GlobalVariables gv = new GlobalVariables();
        String id = gv.id_usuario_firebase;
        gv.fcmToken = token;
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection("users").document(id);
        documentReference.update(Constants.KEY_FCM_TOKEN, token);
    }*/

    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(@NonNull InstanceIdResult instanceIdResult) {
                String updatedToken = instanceIdResult.getToken();
                updateToken(updatedToken);
            }
        });
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_USERS).document(
                preferenceManager.getString(Constants.KEY_ID_USUARIO_FIREBASE)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("No se pudo obtener el token"));

        //Actualizar token en la base de datos

        GlobalVariables gv = new GlobalVariables();
        int id_user = gv.id_usuario;

        actualizarTokenUsuario(id_user, token);
/*
        String url = gv.URLServicio + "sesion.php?usuario="+txtUsuario.getEditText().getText().toString()+
                "&clave="+txtPassword.getEditText().getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    */



    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void actualizarTokenUsuario(int id_usuario, String token){
        GlobalVariables gv = new GlobalVariables();
        String HTTP_URL =gv.URLServicio + "actualizar_token_usuario.php?";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HTTP_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // En caso de tener algun error en la obtencion de los datos

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new Hashtable<String, String>();
                parametros.put("id_usuario", String.valueOf(id_usuario));
                parametros.put("token", token.toString());

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }


}