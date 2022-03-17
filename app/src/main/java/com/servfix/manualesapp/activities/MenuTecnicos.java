package com.servfix.manualesapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.servfix.manualesapp.R;
import com.servfix.manualesapp.fragments.ChatsFragment;
import com.servfix.manualesapp.fragments.ConversacionesFragment;
import com.servfix.manualesapp.fragments.PerfilFragment;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.GlobalVariables;
import com.servfix.manualesapp.utilities.PreferenceManager;

public class MenuTecnicos extends BaseActivity {
    BottomNavigationView mMenu;
    int itemSelected;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tecnicos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSeletedFragment(new ConversacionesFragment(), "Fragment", R.id.menu_tecnicos);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mMenu = (BottomNavigationView) findViewById(R.id.menu_tecnicos);

        mMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.menu_chats & itemSelected != item.getItemId()){
                    showSeletedFragment(new ConversacionesFragment(), "ConversacionesFragment", item.getItemId());
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MenuTecnicos.this, new OnSuccessListener<InstanceIdResult>() {
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
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}