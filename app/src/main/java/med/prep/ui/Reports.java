package med.prep.ui;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class Reports extends AppCompatActivity {


    public String createHeader() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        String firstname = preferences.getString("FirstName", "FirstName");
        String lastname = preferences.getString("LastName", "LastName");
        String birthdate = preferences.getString("BirthDate", "BirthDate");



        // >>> header
        String html = "<head><center>" + "MEDPREP Report</center></head><body>";

        // >>> subject
        html += "<br>";
        html += "<H0 style='color:darkblue'; 'ul'><center><u>" + "Report" + "</u></center></H0>";
        html += "<br>";


        html += "<table width='100%' border='0'>";



        // *** name, birth ***


        html += "<tr>";
        html += "<th colspan='1' style='text-align:left;font-size:9'>" + firstname + " " + lastname + "</th>";
        html += "<th colspan='2' style='text-align:right;font-size:9'>" + birthdate + "</th>";
        html += "</tr>";


        return html;
    }



}
