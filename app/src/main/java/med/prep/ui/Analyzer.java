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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diagram_analyzer_sheet, container, false);


        expo = new DiagramExpose(getContext(), null, null);

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");

        //registerActions(view);


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




        // *** analysis

        long worst_days = 365;
        long best_days = 0;
        long average_days = 0;

        for (UniversalModel model : expo().getStore().getModels()) {


            // *** analyze ***
            long restdays = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);


                Date model_day = sdf.parse(model.getDate());
                Date date = sdf.parse(expo().getStore().today());


                long diffInMillies = Math.abs(date.getTime() - model_day.getTime());
                long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);


                int tagesdosis = Reports.tagesdosis(model);

                long benutzt = days * tagesdosis;

                int vorrat = 0;
                if (!model.getCoordinates().isEmpty()) { vorrat = Integer.parseInt(model.getCoordinates()); }


                long rest = vorrat - benutzt;
                restdays = rest/tagesdosis;

            } catch (Exception exception) {}//analyze




            average_days = (average_days + restdays)/2;

            if (restdays > best_days) {
                best_days = restdays;
            }


            if (restdays < worst_days) {
                worst_days = restdays;
            }

        }

        TextView best = view.findViewById(R.id.result_best);
        TextView worst = view.findViewById(R.id.result_worst);
        TextView average = view.findViewById(R.id.result_average);

        best.setText(Long.toString(best_days));
        worst.setText(Long.toString(worst_days));
        average.setText(Long.toString(average_days));

        TextView tv = view.findViewById(R.id.result_output);

        if (worst_days < emergency) {
            tv.setText("Nachschub erforderlich");
        } else if (worst_days < order) {
            tv.setText("Nachschub empfohlen");
        } else {
            long diff = worst_days - order;

            tv.setText("Bestellen in " + diff + " Tagen");
        }





        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}