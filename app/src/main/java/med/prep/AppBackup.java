package med.prep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class AppBackup extends AppCompatActivity {

    final static String namespace = "medprep.xml";
    final static String ns_backup = "backup.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.system_backup);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(R.string.menu_backup);

        }

        Button backup_save = findViewById(R.id.backup_save);
        backup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DiagramExpose expo = new DiagramExpose(getApplicationContext(), null, null);
                Store sexpo = new DiagramStore(expo, namespace);
                expo.createStore(sexpo, namespace, "");


                DiagramExpose backup = new DiagramExpose(expo.getContext(), null, null);
                Store sbackup = new DiagramStore(backup, ns_backup);
                backup.createStore(sbackup, ns_backup, "");


                backup.getStore().close();

                for (UniversalModel model : expo.getStore().getModels()) {

                    UniversalModel t = backup.getStore().createDefaultModel("title", "subject");

                    t.setDate(model.getDate());
                    t.setType(model.getType());
                    t.setState(model.getState());

                    t.setSymbol(model.getSymbol());
                    t.setTitle(model.getTitle());
                    t.setSubject(model.getSubject());

                    t.setContent(model.getContent());
                    t.setSpecs(model.getSpecs());
                    t.setTags(model.getTags());

                    t.setLocation(model.getLocation());
                    t.setCoordinates(model.getCoordinates());

                    t.setTargets(model.getTargets());

                }

                backup.getStore().saveLocalModel(backup, backup.getFolder());

                Toast.makeText(getApplicationContext(), "backup finished", Toast.LENGTH_LONG).show();
            }
        });



        Button backup_load = findViewById(R.id.backup_restore);
        backup_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DiagramExpose backup = new DiagramExpose(getApplicationContext(), null, null);
                Store sbackup = new DiagramStore(backup, ns_backup);
                backup.createStore(sbackup, ns_backup, "");


                DiagramExpose expo = new DiagramExpose(getApplicationContext(), null, null);
                Store sexpo = new DiagramStore(expo, namespace);
                expo.createStore(sexpo, namespace, "");

                expo.getStore().close();

                for (UniversalModel model : backup.getStore().getModels()) {

                    UniversalModel t = expo.getStore().createDefaultModel("title", "subject");

                    t.setDate(model.getDate());
                    t.setType(model.getType());
                    t.setState(model.getState());

                    t.setSymbol(model.getSymbol());
                    t.setTitle(model.getTitle());
                    t.setSubject(model.getSubject());

                    t.setContent(model.getContent());
                    t.setSpecs(model.getSpecs());
                    t.setTags(model.getTags());

                    t.setLocation(model.getLocation());
                    t.setCoordinates(model.getCoordinates());

                    t.setTargets(model.getTargets());

                }

                expo.getStore().saveLocalModel(backup, backup.getFolder());

                Toast.makeText(getApplicationContext(), "restore finished", Toast.LENGTH_LONG).show();
            }
        });


        Button sample = findViewById(R.id.button);
        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "creating sample database", Toast.LENGTH_LONG).show();

                DiagramExpose expo = new DiagramExpose(getApplicationContext(), null, null);
                Store sexpo = new DiagramStore(expo, namespace);
                expo.createStore(sexpo, namespace, "");

                for (int i=0; i<4; i++) {
                    UniversalModel model = expo.getStore().createDefaultModel("Anwendung", "Name");
                    model.setContent("10 mg");
                    model.setSpecs("N3");
                    model.setTags("08:00");
                    model.setType("0");
                    model.setCoordinates("100");
                    model.setState("0");

                    redateModel(model, expo.getStore().today());
                }

                expo.getStore().saveLocalModel(expo, expo.getFolder());
            }
        });

    }

    private void redateModel(UniversalModel model, String today) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

            Date model_day = sdf.parse(model.getDate());

            long diffInMillies = Math.abs(sdf.parse(today).getTime() - model_day.getTime());
            long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            Calendar c = Calendar.getInstance();
            c.setTime(model_day);
            c.set(Calendar.DATE, -7);

            model.setDate(sdf.format(c.getTime()));

            //expo.getStore().saveLocalModel(expo, expo.getFolder());
            //expo.redraw(true);

        } catch (Exception e) {}
    }
}
