package med.prep.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import med.prep.R;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class Analyzer extends Fragment {

    final static String namespace = "medprep.xml";


    DiagramExpose expo;
    public DiagramExpose expo() { return expo; }

    int emergency = 11;
    int order = 37;

    String fullname;
    String birthdate;


    long worst_days = 365;
    long best_days = 0;
    long average_days = 0;

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        String e = preferences.getString("emergency", "");
        if (!e.isEmpty()) {
            emergency = Integer.parseInt(e);
        }

        String o = preferences.getString("order", "");
        if (!o.isEmpty()) {
            order = Integer.parseInt(o);
        }

        fullname = preferences.getString("FirstName", "") + " " + preferences.getString("LastName", "");
        birthdate = preferences.getString("BirthDate", "");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diagram_analyzer_sheet, container, false);


        expo = new DiagramExpose(getContext(), null, null);

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");


        loadPreferences();

        TextView best = view.findViewById(R.id.result_best);
        TextView worst = view.findViewById(R.id.result_worst);
        TextView average = view.findViewById(R.id.result_average);


        // *** analysis



        for (UniversalModel model : expo().getStore().getModels()) {

            long restdays = Reports.restdays(model, expo.getStore().today());

            average_days = (average_days + restdays)/2;

            if (restdays > best_days) {
                best_days = restdays;
            }


            if (restdays < worst_days) {
                worst_days = restdays;
            }

        }//next model



        // show analysis
        best.setText(Long.toString(best_days));
        worst.setText(Long.toString(worst_days));
        average.setText(Long.toString(average_days));







        //write analytical text
        analyticalText(view);






        return view;
    }

    private void analyticalText(View view) {

        TextView tv = view.findViewById(R.id.result_output);

        if (worst_days < emergency) {

            /* Nachschub umgehend erforderlich */
            tv.setText(getString(R.string.analyze_emergency));




        } else if (worst_days < order) {

            /* Nachschub empfohlen */
            tv.setText(getString(R.string.analyze_stockup));


        } else {



            long diff = worst_days - order;


            // Nachschub in xxx Tagen
            String xxx = getString(R.string.analyze_ok).replace("%s", Long.toString(diff));
            tv.setText(xxx);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}