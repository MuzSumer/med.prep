package med.prep.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import med.prep.R;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.meta.UniversalModel;

public class StockUpDialog extends DialogFragment {

    DiagramExpose diagram;
    UniversalModel model;

    EditText add_bestand, add_zubuchen;


    public StockUpDialog(DiagramExpose set_diagram, UniversalModel set_model) {
        diagram = set_diagram;
        model = set_model;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());




        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_stockup, null);





        builder.setView(view);
        {
            TextView tv = view.findViewById(R.id.dialog_model);
            tv.setText(model.getSubject());



            add_bestand = view.findViewById(R.id.add_bestand);
            add_zubuchen = view.findViewById(R.id.add_zubuchen);



            long days = Reports.days(model, diagram.getStore().today());
            int tagesdosis = Reports.tagesdosis(model);

            long benutzt = days * tagesdosis;

            int vorrat = 0;
            if (!model.getCoordinates().isEmpty()) { vorrat = Integer.parseInt(model.getCoordinates()); }


            long rest = vorrat - benutzt;
            add_bestand.setText(Long.toString(rest));

        }// fields







        String okay = getContext().getString(R.string.dialog_add_confirm);
        builder.setPositiveButton(okay, (dialog, id) -> {

            int saldo = Integer.parseInt(add_bestand.getText().toString());
            int zubuchen = Integer.parseInt(add_zubuchen.getText().toString());

            model.setCoordinates(Integer.toString(saldo + zubuchen));
            model.setDate(diagram.getStore().today());


            diagram.getStore().saveLocalModel(diagram, diagram.getFolder());
            diagram.redraw(true);

        });


        String cancel = getContext().getString(R.string.dialog_cancel);
        builder.setNegativeButton(cancel, (dialog, id) -> {
            model = null;
            dialog.cancel();
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            Button negativeButton = ((AlertDialog) dialog1).getButton(DialogInterface.BUTTON_NEGATIVE);
            Button positiveButton = ((AlertDialog) dialog1).getButton(DialogInterface.BUTTON_POSITIVE);

            negativeButton.setTextColor(Color.RED);
            positiveButton.setTextColor(Color.GREEN);

            negativeButton.invalidate();
            positiveButton.invalidate();
        });


        return dialog;
    }//builder
}
