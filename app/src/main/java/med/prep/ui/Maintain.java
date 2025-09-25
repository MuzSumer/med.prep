package med.prep.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import med.prep.R;
import med.prep.model.DiagramUtil;
import med.prep.model.dialog.StockUp;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class Maintain extends Fragment {

    final static String namespace = "medprep.xml";

    DiagramExpose expo;
    public DiagramExpose expo() { return expo; }


    int emergency = 11;
    int order = 37;

    String fullname;
    String birthdate;

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

        View view = inflater.inflate(R.layout.diagram_maintain, container, false);


        expo = new DiagramExpose(getContext(), view.findViewById(R.id.diagram), view.findViewById(R.id.scroll));

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");

        registerActions(view);


        loadPreferences();


        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        expo().getDiagram().setLayoutManager(manager);

        ModelAdapter adapter = new ModelAdapter(expo().getContext());
        expo().getDiagram().setAdapter(adapter);


        return view;
    }



    private void registerActions(View view) {
        view.findViewById(R.id.record_add).setOnClickListener(
                v -> {

                    UniversalModel model = expo().getSelectedModel();

                    if (model == null) {
                        Toast.makeText(getContext(), R.string.report_select_model, Toast.LENGTH_SHORT).show();
                        expo().beep();
                        return;
                    }

                    //
                    StockUp dialog = new StockUp(expo(), model);
                    dialog.show(getChildFragmentManager(), "");
                }
        );

        view.findViewById(R.id.record_remove).setOnClickListener(
                v -> {
                    //Toast.makeText(getContext(), R.string.diagram_error, Toast.LENGTH_SHORT).show();
                    expo().beep();
                }
        );


        view.findViewById(R.id.record_share).setOnClickListener(
                v -> {

                    boolean create = false;
                    for (UniversalModel model : expo().getStore().getModels()) {
                        long restdays = Reports.restdays(model, expo().getStore().today());

                        if (restdays < order) {
                            create = true;
                        }
                    }//next

                    if (create) {
                        Toast.makeText(getContext(), getString(R.string.report_generation), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(), getString(R.string.action_data_warning), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getContext(), MaintainReport.class);
                        intent.putExtra("namespace", namespace);

                        view.getContext().startActivity(intent);


                    } else {
                        Toast.makeText(getContext(), getString(R.string.report_empty), Toast.LENGTH_SHORT).show();
                    }

                }
        );


        view.findViewById(R.id.record_search).setOnClickListener(
                v -> {

                    boolean create = false;
                    for (UniversalModel model : expo().getStore().getModels()) {
                        long restdays = Reports.restdays(model, expo().getStore().today());

                        if (restdays < order) {
                            create = true;
                        }
                    }//next

                    if (create) {
                        Toast.makeText(getContext(), getString(R.string.action_data_warning), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        //intent.putExtra(Intent.EXTRA_EMAIL, "");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Bedarf");
                        intent.putExtra(Intent.EXTRA_TEXT, body());

                        startActivity(Intent.createChooser(intent, "Send Email"));


                    } else {
                        Toast.makeText(getContext(), getString(R.string.report_empty), Toast.LENGTH_SHORT).show();
                    }




                }
        );
    }


    private String body() {

        String body = fullname + ", " + birthdate + "\n\n";


        for (UniversalModel model : expo().getStore().getModels()) {


            String result = "";


            long restdays = Reports.restdays(model, expo.getStore().today());


            if (restdays < order) {
                result = "noch " + restdays + " Tage";
                //days + " Tage   " + benutzt + "/" + vorrat + " Tabletten"

                if (restdays < emergency) {
                    result = "nur noch " + restdays + " Tage";
                }

                result = model.getSubject() + " " + model.getContent() + ", " + model.getSpecs() + " Stück";
                if (body.isEmpty()) {
                    //body = model.getSubject() + " " + result;
                    body = result;
                } else {
                    //body = body + "\n" + model.getSubject() + " " + result;
                    body = body + "\n" + result;
                }
            }


        }//for


        return body;
    }

    public View.OnClickListener selectCell() { return cellSelect; }


    public View.OnClickListener openCell() { return cellOpen; }


    public View.OnClickListener editCell() { return cellEdit; }



    private final View.OnClickListener cellSelect = view -> {
        String modelId = view.getContentDescription().toString();

        //getDiagram().setSelected(modelId);

        expo().setFocus(modelId, false);

        UniversalModel m = expo().getStore().findModel(modelId);

    };

    private final View.OnClickListener cellEdit = view -> {
        String id = view.getContentDescription().toString();
        UniversalModel model = expo().getStore().findModel(id);

        if (Reports.quickMode(getContext())) {
            expo().setFocus(id, false);
            expo().redraw(true);

            StockUp dialog = new StockUp(expo(), model);
            dialog.show(getChildFragmentManager(), "");

            return;
        }

        UniversalModel focused = expo.getSelectedModel();

        if (focused == null) {
            expo().setFocus(id, false);
            expo().redraw(true);

            return;
        }

        if (model.getId() != focused.getId()) {
            expo().setFocus(id, false);
            expo().redraw(true);

            return;
        }

        if (model.getId() == focused.getId()) {
            StockUp dialog = new StockUp(expo(), model);
            dialog.show(getChildFragmentManager(), "");
        }

    };

    private final View.OnClickListener cellOpen = view -> {
        String id = view.getContentDescription().toString();

        expo().setFocus(id, false);
        expo().redraw(false);


        UniversalModel model = expo().getStore().findModel(id);
        String subject = model.getSubject();


        /*
        Intent intent = new Intent(getActivity(), ViewPlace.class);
        intent.putExtra("namespace", expo().getNamespace());
        intent.putExtra("folder", expo().getFolder());
        intent.putExtra("id", id);

        startActivity(intent);
         */

    };





    private void customItem(DiagramExpose.UniversalModelViewHolder mv, UniversalModel model) {

        //mv.getTitle().setOnClickListener(selectCell());
        //mv.getSubject().setOnClickListener(selectCell());
        //mv.getDate().setOnClickListener(editCell());
        //mv.getType().setOnClickListener(editCell());
        //mv.getState().setOnClickListener(editCell());
        //mv.getOpenLocation().setOnClickListener(openMap());
        //mv.getLocation().setOnClickListener(wrongLocation());
        //mv.getImage().setOnClickListener(editCell());


        // stock up
        mv.getImage().setOnClickListener(editCell());



        // analytic string
        Resources res = getContext().getResources();

        String[] array_type = res.getStringArray(R.array.type_speak);
        ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));

        int index = 0;
        index = Integer.parseInt(model.getType());

        long restdays = Reports.restdays(model, expo.getStore().today());
        String location = types.get(index) + ", noch " + restdays + " Tage";

        mv.getLocation().setTextColor(Color.GRAY);

        //mv.getLocation().setTextColor(ContextCompat.getColor(getContext(), android.R.color.system_primary_light));

        if (restdays < emergency) {
            mv.getLocation().setTextColor(Color.RED);
            location = types.get(index) + ", nur noch " + restdays + " Tage";
        }

        mv.getLocation().setText(location);

    }


    class ModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            UniversalModel model = expo().getStore().getModelAt(position);
            String id = model.getId();


            DiagramExpose.UniversalModelViewHolder mv = (DiagramExpose.UniversalModelViewHolder) holder;


            mv.itemView.setContentDescription(id);
            mv.itemView.setOnClickListener(openCell());



            ConstraintLayout layout = mv.getLayout();
            layout.setContentDescription(id);


            // hovering
            /*
            mv.getLayout().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String id1 = v.getContentDescription().toString();

                    message(id);
                }
            });

            mv.getLayout().setOnHoverListener((view1, event) -> {

                String id1 = view1.getContentDescription().toString();


                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        message("enter " + id1);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        message("move " + id1);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        message("exit " + id1);
                        break;
                }
                return false;
            });
             */




            {
                if (id.equals(expo().getSelected())) {
                    mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_selected));
                } else {
                    mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_background));
                }
            }// selection


            {
                if (id.equals(expo().getSelected())) {
                    layout.post(() -> {
                        int l = layout.getMeasuredWidth()*3/5;
                        // select
                        Drawable d = getContext().getDrawable(R.drawable.item_dot_green);
                        int t = 23;
                        DiagramUtil.setDBounds(d, 16, l, t);

                        // edit
                        Drawable e = getContext().getDrawable(R.drawable.item_dot_blue);
                        t = 77;
                        DiagramUtil.setDBounds(e, 16, l, t);

                        // location
                        Drawable f = getContext().getDrawable(R.drawable.item_dot_red);
                        t = layout.getMeasuredHeight() - 23;
                        DiagramUtil.setDBounds(f, 16, l, t);


                        // image
                        Drawable g = getContext().getDrawable(R.drawable.item_dot_white);
                        l = mv.getImage().getMeasuredWidth()/2;
                        t = mv.itemView.getMeasuredHeight()/2;
                        DiagramUtil.setDBounds(g, 32, l, t);



                        layout.getOverlay().clear();
                        layout.getOverlay().add(d);
                        layout.getOverlay().add(e);
                        layout.getOverlay().add(f);
                        //layout.getOverlay().add(g);
                    });
                } else {
                    layout.post(() -> {
                        int l = layout.getMeasuredWidth()*3/5;

                        Drawable d = getContext().getDrawable(R.drawable.item_dot_yellow);
                        int t = 23;

                        DiagramUtil.setDBounds(d, 16, t, l);


                        layout.getOverlay().clear();
                        layout.getOverlay().add(d);
                        //layout.getOverlay().add(e);
                    });
                }
            }// overlay



            {
                mv.getDate().setText(model.getDate());
                mv.getDate().setContentDescription(id);



                try {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                } catch (Exception e) {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                }


                mv.getType().setContentDescription(id);
                mv.getState().setContentDescription(id);


            }// date, type, state


            {
                mv.getTitle().setText(model.getTitle());
                mv.getTitle().setContentDescription(id);

                mv.getSubject().setText(model.getSubject());
                mv.getSubject().setContentDescription(id);

            }// title, subject


            {
                mv.getContent().setText(model.getContent());
                mv.getSpecs().setText(model.getSpecs());
                mv.getTags().setText(model.getTags());
            }// content, specs, tags


            {
                mv.getOpenLocation().setContentDescription(id);
                mv.getLocation().setContentDescription(id);

                mv.getLocation().setText(model.getLocation());
            }// location


            {
                mv.getImage().setContentDescription(id);
                expo().setImage(mv.getImage(), model.getSymbol(), getResources().getInteger(R.integer.cell_size_small));
            }// image


            customItem(mv, model);

        }







        Context context;
        public ModelAdapter(Context classContext) {
            context = classContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return expo().createViewHolder(LayoutInflater.from(context).inflate(R.layout.item_maintain, parent, false));
        }

        @Override
        public int getItemCount() {
            return expo().getStore().size();
        }


    }//ModelAdapter
}