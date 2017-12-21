package utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.richmedia.imr3.enssat.com.enrichedvideo.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Pierre-Alexis BULOT
 * @author Morgan D'HAESE
 * Created by morgan on 12/12/17.
 */
public class Utils {

    /**
     * Méthode pour marquer un arrêt pendant l'éxecution du programme
     * @param ms Temps de la pause en milliseconde
     */
    public static void pause(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour récupérer les urls à afficher qui se trouvent dans le dossier "res"
     * @param context Contexte de l'application
     * @return Retourne les différents URLs du fichier XML
     */
    public static Map<String,String> getURLs(Context context){
        Map<String,String> map = new HashMap<>();

        XmlPullParser xpp = context.getResources().getXml(R.xml.urls);
        try {
            String valueForNameAttribute = "";
            String textValue = "";

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("url")) {
                        valueForNameAttribute = xpp.getAttributeValue (null,"name");
                    }
                }
                else if(xpp.getEventType() == XmlPullParser.TEXT) {
                    textValue = xpp.getText();
                    map.put(valueForNameAttribute, textValue);
                }

                xpp.next();
            }
        } catch(Exception ex){
            return null;
        }
        return map;
    }

    /**
     * Méthode récupérant le temps où l'on doit changer l'url de la web view.
     * @param activity Activité de l'application
     * @return Retourne le temps pour chaque URL
     */
    public static Map<String,String> getTimes(Activity activity){
        Map<String,String> map = new HashMap<>();

        XmlPullParser xpp = activity.getApplication().getResources().getXml(R.xml.time_events);
        try {
            String valueForNameAttribute = "";
            String textValue = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("time")) {
                        valueForNameAttribute = xpp.getAttributeValue (null,"name");
                    }
                }
                else if(xpp.getEventType() == XmlPullParser.TEXT) {
                    textValue = xpp.getText();
                    map.put(valueForNameAttribute, textValue);
                }

                xpp.next();
            }
        } catch(Exception ex){
            return null;
        }
        return map;
    }

    /**
     * Afficher une notification
     * @param context Contexte de l'application
     * @param URL Message à afficher
     */
    public static void showNotificationURLChange(Context context,String URL){
        Toast.makeText(context, "Loading \""+URL+"\"", Toast.LENGTH_SHORT).show();
    }

    /**
     * Méthode permettant de sauvegarder le temps courante dans les préférences partagées
     * @param context Contexte de l'application
     * @param time Temps de la vidéo à sauvegarder
     * @return True si le commit est bien passée, sinon false
     */
    public static boolean saveCurrentTimeSharedPreferences(Context context, Integer time){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.saving_time_file),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.current_time_video),time);
        return editor.commit();
    }

    /**
     * Méthode permettant de récupérer le temps courant de la vidéo depuis les préférences partagées
     * @param context Contexte de l'application
     * @return Retourne le temps courant, s'il n'est pas trouvé, retourne 0
     */
    public static Integer getSavedCurrentTimeSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.saving_time_file),MODE_PRIVATE);
        return sharedPreferences.getInt(context.getString(R.string.current_time_video),0);
    }

    /**
     * Méthode pour supprimer le temps sauvegardé des préfèrences partagées
     * @param context Contexte de l'application
     * @return True si le commit est bien passée, sinon false
     */
    public static boolean removeCurrentTimeFromSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.saving_time_file),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(context.getString(R.string.current_time_video));
        return editor.commit();
    }

    /**
     * Méthode permettant de savoir si l'utilisateur souhaite sauegarder le temps courant de la vidéo
     * afin qu'elle se recharge à sa dernière position
     * @param context Contexte de l'application
     * @return Retourne True si l'utilisateur souhaite sauvegardé ce temps, sinon False
     */
    public static boolean isTimeSavable(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_save_time),false);
    }

    /**
     * Méthode permettant de récupérer la fréquence de surveillance de la vidéo depuis les préférences.
     * Suivant le device de l'utilisateur, il peut être bon de changer cette fréquence afin que le thread ne demande pas trop de ressources
     * @param context Contexte de l'application
     * @return Retourne le temps souhaité par l'utilisateur
     */
    public static Integer getFrequencyWatch(Context context){
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency","1000"));
    }


}
