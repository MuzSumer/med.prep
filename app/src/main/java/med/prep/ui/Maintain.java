package med.prep.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diagram_maintain, container, false);


        expo = new DiagramExpose(getContext(), view.findViewById(R.id.diagram), view.findViewById(R.id.scroll));

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");

        registerActions(view);


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



        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        expo().getDiagram().setLayoutManager(manager);

        ModelAdapter adapter = new ModelAdapter(expo().getContext());
        expo().getDiagram().setAdapter(adapter);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    private void registerActions(View view) {
        view.findViewById(R.id.record_add).setOnClickListener(
                v -> {
                    //Toast.makeText(getContext(), R.string.diagram_error, Toast.LENGTH_SHORT).show();
                    expo().beep();
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

                    Toast.makeText(getContext(), getString(R.string.report_generation), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), MaintainReport.class);
                    intent.putExtra("namespace", namespace);

                    view.getContext().startActivity(intent);
                }
        );


        view.findViewById(R.id.record_search).setOnClickListener(
                v -> {

                    Toast.makeText(getContext(), getString(R.string.action_email_warning), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    //intent.putExtra(Intent.EXTRA_EMAIL, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Bedarf");
                    intent.putExtra(Intent.EXTRA_TEXT, body());

                    startActivity(Intent.createChooser(intent, "Send Email"));

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



                if (body.isEmpty()) {
                    body = model.getSubject() + " " + result;
                } else {
                    body = body + "\n" + model.getSubject() + " " + result;
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












    class ModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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


            // *** analyze

            long restdays = Reports.restdays(model, expo.getStore().today());

            {


                if (id.equals(expo().getSelected())) {
                    if (restdays < order) {
                        mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_yellow));
                    } else {
                        mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_selected));
                    }
                    mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_selected));
                } else {
                    if (restdays < order) {
                        mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_red));
                    } else {
                        mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_background));
                    }
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
                //mv.getDate().setOnClickListener(editCell());


                try {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                } catch (Exception e) {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                }


                mv.getType().setContentDescription(id);
                mv.getState().setContentDescription(id);

                //mv.getType().setOnClickListener(editCell());
                //mv.getState().setOnClickListener(editCell());

                // TODO stock up
                mv.getImage().setOnClickListener(editCell());

            }// date, type, state


            {
                mv.getTitle().setText(model.getTitle());
                mv.getTitle().setContentDescription(id);
                //mv.getTitle().setOnClickListener(selectCell());



                mv.getSubject().setText(model.getSubject());
                mv.getSubject().setContentDescription(id);
                //mv.getSubject().setOnClickListener(selectCell());


            }// title, subject


            {
                mv.getContent().setText(model.getContent());
                mv.getSpecs().setText(model.getSpecs());
                mv.getTags().setText(model.getTags());
            }// content, specs, tags


            {
                mv.getOpenLocation().setContentDescription(id);
                //mv.getOpenLocation().setOnClickListener(openMap());

                //mv.getLocation().setText(shortLocation(model.getLocation(), 1));
                mv.getLocation().setContentDescription(id);


                String location = model.getSpecs() + ", " + restdays + " Tage";

                mv.getLocation().setText(location);


                // *** analyze



                if (restdays < emergency) {
                    mv.getLocation().setTextColor(Color.RED);

                    location = "nur noch " + restdays + " Tage";
                    mv.getLocation().setText(location);
                }

                //mv.getLocation().setOnClickListener(wrongLocation());
            }// location


            {
                mv.getImage().setContentDescription(id);
                //mv.getImage().setOnClickListener(editCell());


                expo().setImage(mv.getImage(), model.getSymbol(), getResources().getInteger(R.integer.cell_size_small));

            }// image

        }







        Context context;
        public ModelAdapter(Context classContext) {
            context = classContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return expo().createViewHolder(LayoutInflater.from(context).inflate(R.layout.diagram_maintain_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return expo().getStore().size();
        }


    }//ModelAdapter
}