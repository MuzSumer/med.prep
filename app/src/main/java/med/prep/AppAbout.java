package med.prep;

import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;



public class AppAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_about);

        String v = "";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            //String version = pInfo.versionName;
            //int verCode = pInfo.versionCode;

            v = " - r" + pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle("About " + v);

        }
    }

    /*


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String t = getString(R.string.about_app);
            actionBar.setTitle(t + v);

        }
     */
}