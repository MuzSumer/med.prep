package med.prep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class AppBackup extends AppCompatActivity {

    final static String namespace = "medprep.xml";
    DiagramExpose expo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_backup);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(R.string.backup);

        }

        Button backup_save = findViewById(R.id.backup_save);
        backup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "coming soon...", Toast.LENGTH_LONG).show();
            }
        });

        backup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "coming soon...", Toast.LENGTH_LONG).show();
            }
        });



        Button backup_load = findViewById(R.id.backup_restore);

        Button sample = findViewById(R.id.button);
        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "creating sample database", Toast.LENGTH_LONG).show();

                expo = new DiagramExpose(getApplicationContext(), null, null);
                Store store = new DiagramStore(expo, namespace);

                expo.createStore(store, namespace, "");


                for (int i=0; i<4; i++) {
                    UniversalModel model = expo.getStore().createDefaultModel("Anwendungsgebiet", "Name");
                    model.setContent("10 mg");
                    model.setSpecs("N3");
                    model.setTags("08:00");
                    model.setType("0");
                    model.setCoordinates("100");
                    model.setState("0");
                }

                expo.getStore().saveLocalModel(expo, expo.getFolder());
            }
        });

    }

}
