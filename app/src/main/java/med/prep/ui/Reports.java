package med.prep.ui;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import med.prep.model.meta.UniversalModel;

public class Reports extends AppCompatActivity {


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
}
