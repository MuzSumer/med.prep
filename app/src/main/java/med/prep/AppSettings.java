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

            actionBar.setTitle(getString(R.string.app_prefs));
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
            setPreferencesFromResource(R.xml.main_prefs, rootKey);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            onSharedPreferenceChanged(sharedPreferences, "");
        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference first = findPreference("FirstName");
            String vfirst = sharedPreferences.getString("FirstName", "");


            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("FirstName", vfirst);


            editor.apply();

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
