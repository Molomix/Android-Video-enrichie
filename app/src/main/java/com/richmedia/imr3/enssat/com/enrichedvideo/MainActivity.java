package com.richmedia.imr3.enssat.com.enrichedvideo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.Map;

import utils.Utils;
import utils.WatchVideo;

/**
 * @author Pierre-Alexis BULOT
 * @author Morgan D'HAESE
 * Activité principale affichant une vidéo et la description de celle-ci
 */
public class MainActivity extends AppCompatActivity {

    private int currentTime = 0;

    private Map<String,String> times;

    private VideoView video;
    private WebView view;

    /**
     * Thread permettant de surveiller la vidéo
     */
    private WatchVideo watchVideo;

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoPath = getString(R.string.video_url);

        initVideo();
        initWeb();

        times = Utils.getTimes(this);

        startVideoAndLoadURL();

        // Initialisation de thread
        initHandlerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        watchVideo.updateFrequency();

        boolean save = Utils.isTimeSavable(this);
        if(save)
        {
            // Si l'on décommente la ligne suivante, l'utilisateur pourra choisir de positioner la vidéo ou non
            // Il faut alors commenter la ligne permettant de repositionner directement la vidéo

            //showPopChoiceReloadVideoToSavedTime();
            video.seekTo(Utils.getSavedCurrentTimeSharedPreferences(this));
        }

        // Lancement du thread permettant de surveiller la position courante de la vidéo
        handlerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveCurrentTimeSharedPreferences(this,currentTime);

        // On arrête de surveiller la vidéo car nous ne sommes plus sur cette activité
        stopHandlerView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Utils.saveCurrentTimeSharedPreferences(this,currentTime);

        // On arrête de surveiller la vidéo car nous ne sommes plus sur cette activité
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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        } else if(id == R.id.action_chapters) {
            return true;
        } else {
            Integer time = null;
            if (id == R.id.chapter_bgb) {
                time = Integer.parseInt(times.get(getString(R.string.key_big_buck_bunny).toLowerCase()));
            } else if (id == R.id.chapter_title) {
                time = Integer.parseInt(times.get(getString(R.string.key_title).toLowerCase()));
            } else if (id == R.id.chapter_butterfly) {
                time = Integer.parseInt(times.get(getString(R.string.key_butterfly).toLowerCase()));
            } else if (id == R.id.chapter_credits) {
                time = Integer.parseInt(times.get(getString(R.string.key_credits).toLowerCase()));
            }

            // Si l'on a appuyé sur un des chapitres, on met à jour la video
            if(time != null){
                video.seekTo(time);
            }

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
        video = findViewById(R.id.video);
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
        WebViewClient viewClient = new WebViewClient();
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        view.setWebViewClient(viewClient);
    }

    /**
     * Méthode pour démarrer la vidéo et charger la vue web
     */
    private void startVideoAndLoadURL(){
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
     * @param ms Temps en millisecondes pour ajuster la position de la vidéo
     */
    private void startVideoAndLoadURL(final int ms){
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

    /**
     * Stopper la surveillance de la video
     */
    private void stopHandlerView(){
        watchVideo.arreter();
    }

    /**
     * Méthode pour démarrer la surveillance de la vidéo
     */
    private void handlerView(){
        watchVideo.demarrer();
    }

    /**
     * Initialisation du thread permettant avec le vidéo et la vue à modifier en fonction de la position courante de la vidéo
     */
    private void initHandlerView() {
        this.watchVideo = new WatchVideo(this,video,view);
    }

    /**
     * Méthode pour proposer à l'utilisateur d'initialiser la vidéo avec le temps qui a été enregistré
     */
    private void showPopChoiceReloadVideoToSavedTime(){
        Integer position = Utils.getSavedCurrentTimeSharedPreferences(this);

        if(position > 10000){

            final int minutes = position/60000;
            final int secondes = (position/1000)%60;

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
