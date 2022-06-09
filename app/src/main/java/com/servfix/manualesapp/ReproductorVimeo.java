package com.servfix.manualesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.servfix.manualesapp.classes.Manual;
import com.servfix.manualesapp.fragments.CuadroDialogoRankin;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ReproductorVimeo extends AppCompatActivity implements View.OnClickListener {

    private static final String VIMEO_ACCESS_TOKEN = "access token";
    private static String URL_VIMEO = "";

    private PlayerView playerView;
    private SimpleExoPlayer player;

    //Release references
    private boolean playWhenReady = false; //If true the player auto play the media
    private int currentWindow = 0;
    private long playbackPosition = 0;
    Manual manual=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_vimeo);

        //Reference exoplayer view
        playerView = findViewById(R.id.video_view);

        ImageView btnAtrasVimeo = findViewById(R.id.btnAtrasVimeo);

        //VIMDEO_ID = "669513404";
        Intent intent = getIntent();
        manual = (Manual) intent.getExtras().getSerializable("manual");

        URL_VIMEO = manual.getUrl();

        btnAtrasVimeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                finish();

            }
        });

        //Build vimeo configuration
        configVimeoClient();

        if(!(manual.getCalificacion() > 0)){
            calificar();
        }
    } //onCreate


    private void createMediaItem(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
    }


    private void initializePlayer() {

        //To play streaming media, you need an ExoPlayer object.
        //SimpleExoPlayer is a convenient, all-purpose implementation of the ExoPlayer interface.
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        callVimeoAPIRequest();

        //Supply the state information you saved in releasePlayer to your player during initialization.
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare();
    }


    private void callVimeoAPIRequest() {

        String videolink = URL_VIMEO;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //params.put("key", "value");
        //    params.put("more", "data");
        client.get(videolink, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        try {

                            JSONObject jsonObject = new JSONObject(res);
                            JSONObject req = jsonObject.getJSONObject("request");
                            JSONObject files = req.getJSONObject("files");
                            JSONArray progressive = files.getJSONArray("progressive");

                            JSONObject array1 = progressive.getJSONObject(1);
                            String v_url=array1.getString("url");

                            Log.d("URLL ",v_url);

                            createMediaItem(v_url);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

    private void configVimeoClient() {
        /*
        Configuration.Builder configBuilder =
                new Configuration.Builder(MainActivity.VIMEO_ACCESS_TOKEN) //Pass app access token
                        .setCacheDirectory(this.getCacheDir());
        VimeoClient.initialize(configBuilder.build());*/
    }

    @Override
    public void onClick(View v) {
        player.setPlayWhenReady(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();

    }

    private void calificar(){
        FragmentManager fm = getSupportFragmentManager();

        CuadroDialogoRankin editNameDialogFragment = new CuadroDialogoRankin(getApplicationContext(), manual.getId_usuario_manual(), manual.getId_manual());

        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
}