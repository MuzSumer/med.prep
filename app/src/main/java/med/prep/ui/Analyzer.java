package med.prep.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import med.prep.R;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class Analyzer extends Fragment {

    final static String namespace = "medprep.xml";


    DiagramExpose expo;
    public DiagramExpose expo() { return expo; }



    long order = 33;
    long emergency = 11;

    long worst_days = 360;
    long best_days = 0;
    long average_days = 0;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diagram_analyzer, container, false);


        order = ReportsUtil.order(getContext());
        emergency = ReportsUtil.emergency(getContext());


        expo = new DiagramExpose(getContext(), null, null);

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");


        TextView best = view.findViewById(R.id.result_best);
        TextView worst = view.findViewById(R.id.result_worst);
        TextView average = view.findViewById(R.id.result_average);
        TextView load = view.findViewById(R.id.result_load);

        TextView dload = view.findViewById(R.id.d_load);
        dload.setText(ReportsUtil.order(getContext()) + " Tage Vorrat");

        // *** analysis



        for (UniversalModel model : expo().getStore().getModels()) {

            long restdays = ReportsUtil.restdays(model, expo.getStore().today());



            average_days = (average_days + restdays)/2;

            if (restdays > best_days) { best_days = restdays; }

            if (restdays < worst_days) { worst_days = restdays; }


        }//next model



        // show analysis
        best.setText(Long.toString(best_days));
        worst.setText(Long.toString(worst_days));
        average.setText(Long.toString(average_days));



        if (worst_days > order) {
            load.setTextColor(Color.BLACK);
            load.setText("100 %");
        }
        else {
            load.setTextColor(Color.RED);
            load.setText(worst_days + "/" + order);

            double l = (double) (100 * worst_days/order);
            //l += 0.5;
            String v = Double.toString(l);
            int p = v.indexOf(".");


            load.setText(v.substring(0, p) + " %");
        }


        //write analytical text
        analyticalText(view);


        return view;
    }

    private void analyticalText(View view) {

        TextView tv = view.findViewById(R.id.result_output);

        tv.setTextColor(Color.BLUE);

        if (worst_days < emergency) {

            /* Nachschub umgehend erforderlich */
            tv.setText(getString(R.string.analyze_emergency));
            tv.setTextColor(Color.RED);




        } else if (worst_days < order) {

            /* Nachschub baldmöglich empfohlen */
            tv.setText(getString(R.string.analyze_stockup));


        } else {



            long diff = worst_days - order;


            // Nachschub in xxx Tagen
            String xxx = getString(R.string.analyze_ok).replace("%s", Long.toString(diff));
            tv.setText(xxx);

        }
    }


}