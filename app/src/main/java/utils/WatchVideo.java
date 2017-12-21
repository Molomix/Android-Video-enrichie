package utils;

import android.webkit.WebView;
import android.widget.VideoView;

import com.richmedia.imr3.enssat.com.enrichedvideo.MainActivity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Pierre-Alexis BULOT
 * @author Morgan D'HAESE
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
     * @param webView La web vue à raffraichir
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
     * @param pause True pour arrêter le traitement du thread, non pas son éxecution. False pour qu'il réalise son traitement
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

        // La map sera directement trié
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

    /**
     * Méthode de traitement du thread surchargée
     */
    @Override
    public void run() {
        super.run();

        int duration,time;

        // Le thread va s'éxecuter tant que ne l'arrête pas
        while(!stop)
        {
            Utils.pause(frequency);

            duration = video.getDuration();

            if(!pause)
            {
                time = video.getCurrentPosition();

                if(time < duration){
                    mainAct.setCurrentTime(time);

                    // Nous devons être sur le thread principal afin de pouvoir intéragir avec
                    // plusieurs vues de l'activité
                    mainAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int nextTimeCodeURL = 0;
                            int timeCodeURL = 0;

                            Iterator<Integer> itr = mapTimeURL.keySet().iterator();

                            boolean found = false;

                            // On recherche le bon URL en fonction de la position courante de la vidéo
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

                            // Récupération de l'URL en fonction du temps
                            String url = mapTimeURL.get(timeCodeURL);

                            // Si l'url n'est pas 'null' et que ce n'est pas celle courante, on recharge
                            if(url != null && !webView.getUrl().contains(url)) {
                                webView.loadUrl(url);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Méthode pour mettre à jour la fréquence de surveillance de la vidéo
     */
    public void updateFrequency() {
        this.frequency = Utils.getFrequencyWatch(mainAct.getApplicationContext());
    }
}
