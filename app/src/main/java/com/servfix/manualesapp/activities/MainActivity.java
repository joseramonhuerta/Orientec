package com.servfix.manualesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.servfix.manualesapp.R;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;
    Animation topAnim, bottomAnim;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        imagen = findViewById(R.id.imgLogo);
        TextView txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtVersion.setText("Versión " + getVersionApp());


        imagen.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, InicioSesion.class);

                //Pair[] pairs = new Pair[1];
                //pairs[0] = new Pair<View, String>(imagen, "logo_imagen");

                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    //options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();

                }

                //startActivity(intent);
                //finish();




            }
        }, SPLASH_SCREEN);
    }

    public String getVersionApp() {
        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
}