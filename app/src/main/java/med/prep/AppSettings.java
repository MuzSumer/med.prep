package med.prep;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


public class AppSettings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.system_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.app_preferences));
        }

        //PreferenceFragment pref = new PreferenceFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_settings, new PreferenceFragment())
                .commit();


    }

    /* --------------------------------windvolt-------------------------------- */

    public static class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            onSharedPreferenceChanged(sharedPreferences, "");
        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference first = findPreference("FirstName");
            String vfirst = sharedPreferences.getString("FirstName", "");

            first.setTitle(vfirst);


            Preference last = findPreference("LastName");
            String vlast = sharedPreferences.getString("LastName", "");

            last.setTitle(vlast);


            Preference birth = findPreference("BirthDate");
            String vbirth = sharedPreferences.getString("BirthDate", "");

            birth.setTitle(vbirth);


            Preference order = findPreference("order");
            String vorder = sharedPreferences.getString("order", "");

            order.setTitle(vorder);


            Preference emergency = findPreference("emergency");
            String vemergency = sharedPreferences.getString("emergency", "");

            emergency.setTitle(vemergency);


            /*
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("FirstName", vfirst);
            editor.apply();
             */


        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }//PreferenceFragment
}
