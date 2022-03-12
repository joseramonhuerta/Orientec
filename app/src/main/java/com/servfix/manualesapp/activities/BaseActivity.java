package com.servfix.manualesapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servfix.manualesapp.utilities.Constants;
import com.servfix.manualesapp.utilities.PreferenceManager;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database =  FirebaseFirestore.getInstance();
        documentReference = database.collection("users")
                .document(preferenceManager.getString("id_usuario_firebase"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_ENLINIA, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_ENLINIA, 1);
    }
}
