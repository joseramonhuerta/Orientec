package com.servfix.manualesapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

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

public class MenuPrincipal extends BaseActivity {
    BottomNavigationView mMenu;
    int itemSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSeletedFragment(new CursosFragment(), "CursosFragment", R.id.menu_cursos);

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
    }
}