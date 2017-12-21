package utils;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.VideoView;

import com.richmedia.imr3.enssat.com.enrichedvideo.MainActivity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import utils.Utils;

/**
 * Created by morgan on 08/12/17.
 * Cette classe va surveiller le temps courant de la vidéo.
 */
public class WatchVideo extends Thread {

    private VideoView video;
    private WebView webView;
    private MainActivity mainAct;

    private Integer frequency;

    /**
     * Map qui associe le temps où l'on doit charger une nouvelle URL, à cette dite URL
     */
    private Map<Integer,String> mapTimeURL;
    private boolean pause;
    private boolean stop;

    /**
     * Constructeur prenant en compte l'activité, la vidéo à surveiller, la vue à raffraichir.
     * @param activity L'activité où se trouve la vidéo et la web view
     * @param video La vidéo à surveiller
     * @param webView La web vue, à corriger
     */
    public WatchVideo(MainActivity activity, VideoView video, WebView webView){
        this.mainAct = activity;
        this.video = video;
        this.webView = webView;

        this.pause = false;
        this.stop = false;

        this.frequency = Utils.getFrequencyWatch(activity.getApplicationContext());

        initMapTimeURL();
    }

    /**
     * Méthode pour démarrer le thread
     */
    public void demarrer(){
        this.stop = false;
        if(!this.isAlive()){
            this.start();
        }

    }

    /**
     * Méthode pour arrêter le thread
     */
    public void arreter(){
        this.stop = true;
    }

    /**
     * Méthode pour mettre le thread en 'pause'.
     * C'est à dire qu'il sera actif, mais qu'il ne fera pas son traitement.
     * Il est en attente.
     * @param pause
     */
    public void pause(boolean pause){
        this.pause = pause;
    }

    /**
     * Méthode d'initialisation de la map associant un temps à une URL
     */
    private void initMapTimeURL(){
        Map<String,String> urls  = Utils.getURLs(this.mainAct);
        Map<String,String> times = Utils.getTimes(this.mainAct);

        Set<String> keys = urls.keySet();
        for (String key: times.keySet())
        {
            if(!keys.contains(key))
                keys.add(key);
        }

        Log.v("KEYS",keys.toString());

        // Tri directement
        mapTimeURL = new TreeMap<>();

        for (String key:keys)
        {
            String url = urls.get(key);
            String time = times.get(key);
            if(url != null && time != null)
            {
                mapTimeURL.put(Integer.parseInt(time),url);
            }
        }
    }

    @Override
    public void run() {
        super.run();

        int duration,time;

        Log.v("WatchVideo","Starting thread");
        while(!stop)
        {
            duration = video.getDuration();

            if(!pause)
            {
                time = video.getCurrentPosition();
                // Attendre que la vidéo soit lancée pour la surveiller
                if(!video.isPlaying()) {
                    Utils.pause(frequency);
                } else if(time < duration){
                    //Log.v("currentDuration",Integer.toString(time));

                    Utils.pause(frequency);

                    mainAct.setCurrentTime(time);

                    mainAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int nextTimeCodeURL = 0;
                            int timeCodeURL = 0;

                            Iterator<Integer> itr = mapTimeURL.keySet().iterator();

                            boolean found = false;

                            while(!found){
                                timeCodeURL = nextTimeCodeURL;
                                if(itr.hasNext()){
                                    nextTimeCodeURL = itr.next();
                                    if(nextTimeCodeURL > video.getCurrentPosition()){
                                        found = true;
                                    }
                                }else {
                                    // on a terminé la liste
                                    found = true;
                                }
                            }

                            //Log.v("TimeCodeURL",""+timeCodeURL);

                            String url = mapTimeURL.get(timeCodeURL);

                            //Log.v("URL","URL TIME CODE "+url);

                            if(url != null && !webView.getUrl().contains(url)) {
                                //Log.v("ChangeURL","Loading "+url);
                                //Toast.makeText(mainAct,"Loading "+url,Toast.LENGTH_SHORT).show();
                                webView.loadUrl(url);
                            }
                        }
                    });
                }
            }
        }
        Log.v("Video","Videoover");
    }

    public void updateFrequency() {
        this.frequency = Utils.getFrequencyWatch(mainAct.getApplicationContext());
        Log.v("Frequency",this.frequency.toString());
    }
}
