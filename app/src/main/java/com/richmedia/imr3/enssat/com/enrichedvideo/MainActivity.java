package com.richmedia.imr3.enssat.com.enrichedvideo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.Map;

import utils.Utils;
import utils.WatchVideo;


public class MainActivity extends AppCompatActivity {

    private int currentTime = 0;

    private Map<String,String> times;

    //private Handler handler

    private VideoView video;
    private WebView view;
    private WebViewClient viewClient;

    private WatchVideo watchVideo;

    //private String videoPath = "http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_surround.avi";
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoPath = getString(R.string.video_url);

        Log.v("OnCreate","CREATION");
        Log.v("OnCreate",videoPath);
        Log.v("OnCreate","#----------------------------#");

        initVideo();
        initWeb();

        times = Utils.getTimes(this);

        Log.v("Video","Starting video");
        startVideoAndLoadURL();

        initHandlerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("OnResume","RESUME");
        watchVideo.updateFrequency();

        boolean save = Utils.isTimeSavable(this);
        //Toast.makeText(this,"Sauvegarde du temps de la vidéo ? "+save,Toast.LENGTH_LONG).show();
        if(save)
        {
            //showPopChoiceReloadVideoToSavedTime();
            video.seekTo(Utils.getSavedCurrentTimeSharedPreferences(this));
        }

        handlerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("OnPause","PAUSE");

        Log.v("OnPause","Position " + video.getCurrentPosition());

        Utils.saveCurrentTimeSharedPreferences(this,currentTime);

        stopHandlerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("OnStop","STOP");

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Log.v("OnStop","Pref "+sharedPreferences.getAll());
        Log.v("OnStop","CurrentTime "+currentTime);

        Utils.saveCurrentTimeSharedPreferences(this,currentTime);

        Log.v("OnStop","Position " + video.getCurrentPosition());

        stopHandlerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("OnDestroy","DESTROY");
        Utils.removeCurrentTimeFromSharedPreferences(this);
        stopHandlerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_chapters) {
            return true;
        } else {
            Integer time = 0;
            Log.v("TIME",times.toString());
            if (id == R.id.chapter_bgb) {
                time = Integer.parseInt(times.get(getString(R.string.big_buck_bunny).toLowerCase()));
            } else if (id == R.id.chapter_title) {
                time = Integer.parseInt(times.get(getString(R.string.title).toLowerCase()));
            } else if (id == R.id.chapter_butterfly) {
                time = Integer.parseInt(times.get(getString(R.string.butterfly).toLowerCase()));
            } else if (id == R.id.chapter_credits) {
                time = Integer.parseInt(times.get(getString(R.string.credits).toLowerCase()));
            }
            video.seekTo(time);
            return true;
        }

        //return super.onOptionsItemSelected(item);
    }

      //////////////////////////////////////////
     //      Méthodes non-surchargées        //
    //////////////////////////////////////////

    /**
     * Initialisation des élements liées à la vidéo
     */
    private void initVideo(){
        //TODO Récupérer une référence sur la vue ajoutée dans le layout activity_main
        video = findViewById(R.id.video);
        //TODO Associer un MediaController à la vue ajoutée
        MediaController controller = new MediaController(video.getContext());
        video.setVideoPath(videoPath);
        controller.setAnchorView(video);
        video.setMediaController(controller);
    }

    /**
     * Initialisation des élements liées à la vue web
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb(){
        view = findViewById(R.id.web);
        viewClient = new WebViewClient();
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        view.setWebViewClient(viewClient);
    }

    /**
     * Méthode pour démarrer la vidéo et charger la vue web
     */
    private void startVideoAndLoadURL(){
        //TODO Démarrer la lecture de la vidéo
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                video.start();
                view.loadUrl(videoPath);
            }
        });
    }

    /**
     * Méthode pour démarrer la vidéo quand elle est prête à un certain moment donnée de celle-ci
     * @param ms
     */
    private void startVideoAndLoadURL(final int ms){
        //TODO Démarrer la lecture de la vidéo
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                video.seekTo(ms);
                video.start();
                view.loadUrl(videoPath);
            }
        });
    }

    /**
     * Méthode pour changer la position courante de la vidéo
     * @param minutes Minutes de la vidéo
     * @param secondes Secondes de la vidéo
     */
    private void setPositionVideo(int minutes, int secondes){
        int time = minutes * 60 * 1000 + secondes * 1000;
        video.seekTo(time);
    }

    private void setVideoSavedPosition(){

    }

    private void stopHandlerView(){
        watchVideo.arreter();
    }

    private void handlerView(){
        watchVideo.demarrer();
    }

    private void initHandlerView() {
        this.watchVideo = new WatchVideo(this,video,view);
    }

    /**
     * Getting chapters by time
     * @return
     */
    private Map<String,String> getTimeChapters(){
        LinearLayout ll = findViewById(R.id.chapter_layout);
        View v;
        String tag, text, content;
        String[] split;

        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < ll.getChildCount(); i++){
            v = ll.getChildAt(i);
            tag = v.getTag().toString();
            Log.v("Tag_V",tag);
            if(tag.equals("chapter")){
                Button button = (Button)v;
                text = button.getText().toString();
                content = button.getContentDescription().toString();
                Log.v("InfoBouton",text +","+content);
                split = content.split(":");
                Log.v("InfoBouton",text +","+content);
                final int minutes = Integer.parseInt(split[0]);
                final int secondes = Integer.parseInt(split[1]);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setPositionVideo(minutes,secondes);
                    }
                });

                map.put(text,content);
            }
        }

        return map.isEmpty() ? null : map;
    }

    /**
     *
     */
    private void showPopChoiceReloadVideoToSavedTime(){
        Integer position = Utils.getSavedCurrentTimeSharedPreferences(this);

        if(position > 10000){

            final int minutes = position/60000;
            final int secondes = (position/1000)%60;

            Log.v("OnResume","Position " + position);
            Log.v("OnResume","Minutes " + minutes+", secondes "+secondes);

            final AlertDialog.Builder popup = new AlertDialog.Builder(this);
            popup.setTitle("Une position a été enregistrée lors l'arrêt de l'application.\nVoulez-vous recharger la vidéo à cette endroit ?");
            popup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setPositionVideo(minutes,secondes);
                }
            });

            popup.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.saveCurrentTimeSharedPreferences(getApplicationContext(),currentTime);
                }
            });

            popup.show();
        }
    }


      /////////////////////////
     //  SETTER AND GETTER  //
    /////////////////////////

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
    public int getCurrentTime() {
        return currentTime;
    }
}
