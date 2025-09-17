package med.prep;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppWelcome extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.app_overview,
                R.id.app_analyzer,
                R.id.app_maintain
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);

        MenuCompat.setGroupDividerEnabled(menu, true);
        /*

        MenuItem mode = menu.findItem(R.id.mode_switch);
        if (mode != null) {

            mode.setChecked(true);
            if (mode.isChecked()) message("on");
            else message("off");
        }

        mode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                beep();
                return false;
            }
        });
         */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // show settings
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, AppSettings.class);
            startActivity(intent);

            return true;
        }



        // show about
        if (id == R.id.action_about) {

            startActivity(new Intent(this, AppAbout.class));
            return true;
        }

        // show backup
        if (id == R.id.action_backup) {

            startActivity(new Intent(this, AppBackup.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }//menu

}