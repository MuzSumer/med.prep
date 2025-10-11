package med.prep.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import med.prep.model.impl.DiagramExpose;
import med.prep.model.meta.UniversalModel;

public class ReportsUtil extends AppCompatActivity {


    public static long order(Context context) {
        long order = 33;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


        String o = preferences.getString("order", "");
        if (!o.isEmpty()) {
            order = Long.parseLong(o);
        }

        return order;
    }
    public static long emergency(Context context) {
        long emergency = 11;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


        String e = preferences.getString("emergency", "");
        if (!e.isEmpty()) {
            emergency = Long.parseLong(e);
        }

        return emergency;
    }

    public static String UserName(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString("FirstName", "") + " " + preferences.getString("LastName", "");
    }

    public static String BirthDate(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString("BirthDate", "");
    }

    public static boolean quickMode(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean quickmode = preferences.getBoolean("quick_mode", false);

        return quickmode;
    }

    public static boolean speakMode(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean speakmode = preferences.getBoolean("speak_mode", false);

        return speakmode;
    }

    public String createHeader(String title) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        String firstname = preferences.getString("FirstName", "FirstName");
        String lastname = preferences.getString("LastName", "LastName");
        String birthdate = preferences.getString("BirthDate", "BirthDate");



        // >>> header
        String html = "<head><center>" + "MEDPREP" + "</center></head><body>";

        // >>> subject
        html += "<br>";
        html += "<H0 style='color:darkblue'; 'ul'><center><u>" + title + "</u></center></H0>";
        html += "<br>";


        html += "<table width='100%' border='0'>";



        // *** name, birth ***


        html += "<tr>";
        html += "<th colspan='1' style='text-align:left;font-size:13'>" + firstname + " " + lastname + "</th>";
        html += "<th colspan='2' style='text-align:right;font-size:13'>" + birthdate + "</th>";
        html += "</tr>";


        return html;
    }




    public static long days(UniversalModel model, String today) {
        long days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);


            Date model_day = sdf.parse(model.getDate());
            Date date = sdf.parse(today);


            long diffInMillies = Math.abs(date.getTime() - model_day.getTime());
            days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (Exception e) {}

        return days;
    }

    public static int tagesdosis(UniversalModel model) {

        int tagesdosis = 0;

        int type = 0;
        if (!model.getType().isEmpty()) {
            type = Integer.parseInt(model.getType());
        }

        switch (type) {

            case 0:
                tagesdosis = 1;
                break;

            case 1:
                tagesdosis = 2;
                break;

            case 2:
                tagesdosis = 3;
                break;

            case 3:
                tagesdosis = 3;
                break;

            case 4:
                tagesdosis = 5;
                break;


            case 5:
                tagesdosis = 1;
                break;

            case 6:
                tagesdosis = 1;
                break;

            case 7:
                tagesdosis = 2;
                break;

            default:
                tagesdosis = 0;
        }

        return tagesdosis;
    }



    public static long rest(DiagramExpose expo, UniversalModel model) {
        long days = ReportsUtil.days(model, expo.getStore().today());

        int tagesdosis = ReportsUtil.tagesdosis(model);
        long benutzt = days * tagesdosis;

        int vorrat = 0;
        if (!model.getCoordinates().isEmpty()) { vorrat = Integer.parseInt(model.getCoordinates()); }

        return vorrat - benutzt;
    }
    public static long restdays(UniversalModel model, String today) {
        long restdays = 0;

        long days = days(model, today);
        int tagesdosis = tagesdosis(model);


        long benutzt = days * tagesdosis;

        int vorrat = 0;
        if (!model.getCoordinates().isEmpty()) { vorrat = Integer.parseInt(model.getCoordinates()); }


        long rest = vorrat - benutzt;
        restdays = rest/tagesdosis;

        return restdays;
    }



    public static String analysis(DiagramExpose expo, UniversalModel model, long trigger) {

        String result = "";


        long restdays = restdays(model, expo.getStore().today());

        result = ", noch " + restdays + " Tage";

        if (restdays < trigger) {
            result = ", nur noch " + restdays + " Tage";
        }

        return result;
    }
}
